package hr.ferit.antoniocarevic.jambmaster.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import hr.ferit.antoniocarevic.jambmaster.R
import hr.ferit.antoniocarevic.jambmaster.ui.theme.BluePrimary
import hr.ferit.antoniocarevic.jambmaster.ui.theme.OrangeSecondary
import hr.ferit.antoniocarevic.jambmaster.ui.theme.playFontFamily


data class Player(val id: Int, var name: String)

@Composable
fun PlayOptions(
    navController: NavController,
) {
    val players = remember { mutableStateListOf(Player(1, "Player 1")) }
    val maxPlayers = 6

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BluePrimary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_jambmaster3),
            contentDescription = "JambMaster Logo",
            modifier = Modifier
                .size(125.dp)
                .padding(bottom = 16.dp, top = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        players.forEachIndexed { index, player ->
            Card(
                modifier = Modifier
                    .width(200.dp)
                    .height(100.dp)
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Player Icon",
                        tint = BluePrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = player.name,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedBorderColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White
                        ),
                        onValueChange = { newName ->
                            players[index] = player.copy(name = newName)
                        },
                        modifier = Modifier
                            .width(120.dp),
                        textStyle = LocalTextStyle.current.copy(
                            fontFamily = playFontFamily,
                            fontWeight = FontWeight.Medium,
                            color = BluePrimary,
                            fontSize = 18.sp
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Button(
                onClick = {
                    if (players.size < maxPlayers) {
                        val newPlayerId = players.size + 1
                        players.add(Player(newPlayerId, "Player $newPlayerId"))
                    }
                },
                enabled = players.size < maxPlayers,
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangeSecondary,
                    contentColor = Color.White,
                    disabledContainerColor = OrangeSecondary.copy(alpha = 0.5f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .size(width = 75.dp, height = 100.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "+",
                    fontFamily = playFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                )
            }
            Button(
                onClick = {
                    val playerNames = players.joinToString(",") { it.name }
                    navController.navigate("playing_screen/$playerNames")
                },
                enabled = players.isNotEmpty(),
                modifier = Modifier
                    .size(width = 160.dp, height = 100.dp)
                    .padding(bottom = 32.dp, start = 12.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangeSecondary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Start Game",
                    fontFamily = playFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }
    }
}