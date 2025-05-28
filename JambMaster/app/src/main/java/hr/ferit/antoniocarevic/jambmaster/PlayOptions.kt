package hr.ferit.antoniocarevic.jambmaster

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import hr.ferit.antoniocarevic.jambmaster.ui.theme.BluePrimary
import hr.ferit.antoniocarevic.jambmaster.ui.theme.OrangeSecondary
import hr.ferit.antoniocarevic.jambmaster.ui.theme.playFontFamily


data class Player(val id: Int, var name: String)

@Composable
fun PlayOptions(
    navController: NavController,
    modifier: Modifier = Modifier
) {

    val players = remember { mutableStateListOf(Player(1, "Player 1")) }
    val maxPlayers = 6

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = BluePrimary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Title
        Text(
            text = "JambMaster",
            color = Color.White,
            fontSize = 36.sp,
            fontFamily = playFontFamily,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, bottom = 32.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        players.forEachIndexed { index, player ->
            Card(
                modifier = Modifier
                    .width(200.dp)
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
                        value = "Set user...",
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedBorderColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
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
                .size(width = 140.dp, height = 100.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Add Player",
                fontFamily = playFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}