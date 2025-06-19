package hr.ferit.antoniocarevic.jambmaster.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import hr.ferit.antoniocarevic.jambmaster.R
import hr.ferit.antoniocarevic.jambmaster.ui.theme.BluePrimary
import kotlinx.coroutines.delay

@Composable
fun BegginingScreen(navController: NavHostController) {

    LaunchedEffect(Unit) {
        delay(2000)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            navController.navigate("start") {
                popUpTo("beginning") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("beggining") { inclusive = true }
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BluePrimary),
        verticalArrangement = Arrangement.Bottom
    ) {
        Image(painter = painterResource(id = R.drawable.loadingpic),
            contentDescription = "JambMaster Logo",
            modifier = Modifier
                .width(700.dp))
    }
}
