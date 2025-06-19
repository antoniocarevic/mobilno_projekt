package hr.ferit.antoniocarevic.jambmaster.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import hr.ferit.antoniocarevic.jambmaster.R
import hr.ferit.antoniocarevic.jambmaster.ui.theme.BluePrimary
import hr.ferit.antoniocarevic.jambmaster.ui.theme.OrangeSecondary
import hr.ferit.antoniocarevic.jambmaster.ui.theme.playFontFamily

@Composable
fun StartScreen(
    navController: NavController,

) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BluePrimary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_jambmaster3),
            contentDescription = "JambMaster Logo",
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to JambMaster",
                    fontFamily = playFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = BluePrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Challenge friends or play solo in this exciting dice game. Track your scores, compete, and become the ultimate Jamb champion!",
                    fontFamily = playFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = BluePrimary,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(
            modifier = Modifier
                .height(100.dp)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("play_options") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangeSecondary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(100.dp)
                    .width(150.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Play",
                    fontFamily = playFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Button(
                onClick = { navController.navigate("played_games") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangeSecondary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(100.dp)
                    .width(150.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Results",
                    fontFamily = playFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo("start") { inclusive = true }
                    } },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(50.dp)
                    .width(95.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Logout",
                    fontFamily = playFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

        }
    }
}