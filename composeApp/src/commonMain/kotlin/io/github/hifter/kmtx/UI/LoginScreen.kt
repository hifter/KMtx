package io.github.hifter.kmtx.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import io.github.aakira.napier.Napier
import io.github.hifter.kmtx.module.*
import io.ktor.http.URLParserException
import io.ktor.http.Url
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.folivo.trixnity.client.MatrixClient
import net.folivo.trixnity.client.login
import net.folivo.trixnity.clientserverapi.client.MatrixClientServerApiClientImpl
import net.folivo.trixnity.clientserverapi.model.authentication.DiscoveryInformation.HomeserverInformation
import net.folivo.trixnity.clientserverapi.model.authentication.IdentifierType
import net.folivo.trixnity.core.model.UserId

@Composable
fun LoginScreen() {
    val TAG = "KMtxLoginScreen"

    lateinit var matrixRestClient:MatrixClientServerApiClientImpl
//    var homeserver by remember { mutableStateOf("chat.neboer.site") }
    var homeserver by remember { mutableStateOf("http://192.168.1.12:8008") }
    var verifiedHomeserver by remember { mutableStateOf("") }
    lateinit var mVerifiedHomeserver :HomeserverInformation
    var isHomeserverVerified by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("user1") }
    var verifiedUsername by remember { mutableStateOf("") }
    var isUsernameVerified by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("user$1passw0rd") }
    var confirmPassword by remember { mutableStateOf("") }
    var isRegisterMode by remember { mutableStateOf(false) }
    var errorMassage by remember { mutableStateOf("") }
    var homeserverUrl:Url

    suspend fun verifyHomeserver(): Unit {
            // Build proper URL format
            var homeserverBaseUrl = homeserver
            if (!homeserver.startsWith("http://") && !homeserver.startsWith("https://")) {
                // Remove any existing protocol prefix and add HTTPS
                homeserverBaseUrl = homeserver.replace("://", "")
                homeserverBaseUrl = "https://$homeserverBaseUrl"
            }
            try {
                Napier.d(tag = TAG, message = "Verifying homeserver: $homeserverBaseUrl")
                homeserverUrl = Url(homeserverBaseUrl)
                val client = MatrixClientServerApiClientImpl(baseUrl = homeserverUrl)
                val result = client.discovery.getWellKnown()
                result.fold(
                    onSuccess = { response ->
                        Napier.d(tag = TAG, message = "Server WellKnown response: $response")
                        matrixRestClient = client
                        mVerifiedHomeserver = response.homeserver
                        isHomeserverVerified = true
                        verifiedHomeserver = homeserver
                    },
                    onFailure = { exception ->

                        Napier.d(tag = TAG, message = "Failed Server WellKnown response: ${exception.message}")
                        exception.message?.let {
                            if (it.contains("404")){
                                val result = client.server.getVersions()
                                result.fold(
                                    onSuccess = { response ->
                                        Napier.d(tag = TAG, message = "Server getVersions response: $response")
                                        matrixRestClient = client
                                        mVerifiedHomeserver = HomeserverInformation(homeserverBaseUrl)
                                        isHomeserverVerified = true
                                        verifiedHomeserver = homeserver
                                    },
                                    onFailure = { exception ->
                                        errorMassage = "家服务器验证失败，该URL可能不是一个有效的 Matrix 服务器。"
                                    }
                                )
                            }
                        }
                    }
                )
                if (result.isSuccess) {
                    Napier.d(tag = TAG, message = "Homeserver WellKnown successful")
                } else {
                    Napier.d(tag = TAG, message = "Homeserver verification failed - WellKnown request unsuccessful")
                }
            } catch (e: URLParserException) {
                Napier.e(tag = TAG, message = "Invalid URL format: ${e.message}")
                errorMassage = "URL格式错误"
            } catch (e: Exception) {
                Napier.e(tag = TAG, message = "Unexpected error during verification: ${e.message}")
                errorMassage = "家服务器验证出现未知错误"
            }

    }
    val verifyRegisterUsername:() -> Unit = {
        if (username == ""){
            errorMassage = "userid不能为空"
        }else if (isHomeserverVerified){
            CoroutineScope(Dispatchers.IO).launch {
                val result = matrixRestClient.authentication.isUsernameAvailable(username)
                result.fold(
                    onSuccess = {
                        Napier.d(tag = TAG, message = "Server isUsernameAvailable response: $it")
                        if (it){
                            isUsernameVerified = true
                            verifiedUsername = username
                        }else{
                            errorMassage = "该userid不可用"
                        }
                    },
                    onFailure = {
                        Napier.d(tag = TAG, message = "Server isUsernameAvailable response: $it")
                        errorMassage = "未知错误或者该userid不可用 $it"
                    }
                )
            }
        }else{
            errorMassage = "家服务器未验证"
        }
    }
    fun onRegister():Unit {
        errorMassage = "未实现注册功能，请转移至其他客户端完成"
    }
    val validatePassword:(String) -> Boolean = {
        it.length>=8
    }
    fun onLogin():Unit {
        if (isRegisterMode){
            onRegister()
            return
        }
        if (!validatePassword(password)){
            errorMassage = "密码至少八位"
            return
        }
        if (username == ""){
            errorMassage = "用户名不能为空"
            return
        }
        CoroutineScope(Dispatchers.IO).launch{
            if (homeserver != verifiedHomeserver || !isHomeserverVerified){
                verifyHomeserver()
                if (homeserver != verifiedHomeserver || !isHomeserverVerified)
                    return@launch
            }
            val baseurl = Url(mVerifiedHomeserver.baseUrl)
            val tmpUserid = "@$username:${baseurl.host}"
            val client = MatrixClient.login(
                baseUrl = baseurl,
                identifier = IdentifierType.User(username),
                password = password,
                repositoriesModule = RepositoriesModule.getRepositoriesModule(UserId(tmpUserid)),
                mediaStoreModule = RepositoriesModule.getMediaStoreModule(),
                deviceId = PlatformInfoModule.getDeviceInfo()
            ).getOrNull()
            Napier.d(tag = TAG, message = "MatrixClient.login(\n" +
                    "RepositoriesModule UserId @$username:${baseurl.host} \n" +
                    "deviceId = ${PlatformInfoModule.getDeviceInfo()} \n" +
                    ")")
            if (client == null){
                errorMassage = "登陆失败"
                Napier.d(tag = TAG, message = "login failed")
            }else{
                val userid = client.userId.toString()
                if (tmpUserid != userid){
                    val client2 = MatrixClient.login(
                        baseUrl = baseurl,
                        identifier = IdentifierType.User(username),
                        password = password,
                        repositoriesModule = RepositoriesModule.getRepositoriesModule(UserId(userid)),
                        mediaStoreModule = RepositoriesModule.getMediaStoreModule(),
                        deviceId = PlatformInfoModule.getDeviceInfo()
                    ).getOrNull()
                    if (client2 == null) {
                        errorMassage = "未知错误登陆失败"
                        Napier.d(tag = TAG, message = "login failed")
                        return@launch
                    }
                    Napier.d(tag = TAG, message = "MatrixClient.login(\n" +
                            "RepositoriesModule UserId $userid \n" +
                            "deviceId = ${PlatformInfoModule.getDeviceInfo()} \n" +
                            ")")
                    Napier.d(tag = TAG, message = "login success ${client2.userId}")
                    loginSuccess(client2)
                    return@launch
                }
                Napier.d(tag = TAG, message = "login success ${client.userId}")
                loginSuccess(client)
            }
        }
    }
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .statusBarsPadding()
                    ,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isRegisterMode) "Matrix 注册" else "Matrix 登陆",
                style = MaterialTheme.typography.titleLarge
            )
// Homeserver input row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = homeserver,
                    onValueChange = {
                        homeserver = it
                        isHomeserverVerified = false
                    },
                    label = { Text("家服务器地址") },
                    placeholder = { Text("例如：matrix.example.com") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Done
                    )
                )
                Button(
                    onClick = {CoroutineScope(Dispatchers.IO).launch {verifyHomeserver()}},
                    enabled = homeserver != verifiedHomeserver || !isHomeserverVerified
                ) {
                    if (homeserver == verifiedHomeserver && isHomeserverVerified) {
                        Text("已验证")
                    } else {
                        Text("验证")
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("用户名") },
                    placeholder = { Text("请输入用户名") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )
                if (isRegisterMode){
                    Button(
                        onClick = verifyRegisterUsername,
                        enabled = username != verifiedUsername || !isUsernameVerified
                    ){
                        if(username == verifiedUsername && isUsernameVerified){
                            Text("已验证")
                        }else{
                            Text("验证")
                        }
                    }
                }
            }
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("密码") },
                placeholder = { Text("请输入密码") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = if (isRegisterMode) ImeAction.Next else ImeAction.Done
                ),
                isError = !validatePassword(password)
            )
            if (isRegisterMode) {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("确认密码") },
                    placeholder = { Text("请再次输入密码") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    isError = !validatePassword(confirmPassword) || password != confirmPassword,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = ::onLogin,
                    enabled = homeserver!="" && username != "" && validatePassword(password) &&
                            (!isRegisterMode || password == confirmPassword),
                ) {
                    Text(if (isRegisterMode) "注册" else "登录")
                }
                TextButton(
                    onClick = {
                        errorMassage = "未实现注册功能，请转移至其他客户端完成"
//                        isRegisterMode = !isRegisterMode
//                        if (!isRegisterMode) {
//                            confirmPassword = ""
//                        }
                    }
                ) {
                    Text(if (isRegisterMode) "返回登录" else "注册账号")
                }
            }
            if (errorMassage != ""){
                Text(text = errorMassage,
                    color = Color.Red,
                    modifier = Modifier.background(color = Color.Yellow)
                )
            }
        }
    }
}
fun loginSuccess(client: MatrixClient) {
    val userid = client.userId.toString()
    CoroutineScope(Dispatchers.IO).launch {
        AppStateModule.addUser(userid)
        Napier.d(tag = "KMtxLoginScreen", message = "LoginSuccess: User $userid added")
    }
}