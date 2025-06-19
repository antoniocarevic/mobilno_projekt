package hr.ferit.antoniocarevic.jambmaster.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import hr.ferit.antoniocarevic.jambmaster.R
import hr.ferit.antoniocarevic.jambmaster.ui.theme.BluePrimary
import hr.ferit.antoniocarevic.jambmaster.ui.theme.OrangeSecondary

@Composable
fun RegisterScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
                .size(250.dp)
                .padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = Color.White) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedBorderColor = Color.Gray,
                unfocusedTextColor = Color.Gray,
                focusedBorderColor = Color.White,

                ),
            modifier = Modifier
                .width(315.dp)
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", color = Color.White) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedBorderColor = Color.Gray,
                unfocusedTextColor = Color.Gray,
                focusedBorderColor = Color.White
            ),
            modifier = Modifier
                .width(315.dp)
                .padding(bottom = 16.dp)
        )

        val context = LocalContext.current

        Button(
            onClick = { Firebase.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Toast.makeText(context, "Registration is successful! Please log in.", Toast.LENGTH_SHORT).show()
                        navController.navigate("login"){
                            popUpTo("register") {inclusive = true}
                        }
                    }
                    else {
                        Toast.makeText(context, task.exception?.message ?: "Registration failed!", Toast.LENGTH_SHORT ).show()
                    }
                }},
            colors = ButtonDefaults.buttonColors(
                containerColor = OrangeSecondary,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(150.dp)
                .padding(bottom = 16.dp)
        ) {
            Text("Register")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                text = "You already have an account? Login ",
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Text(
                text = "here!",
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 16.sp,
                style = TextStyle(
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier
                    .clickable { navController.navigate("login"){
                        popUpTo("register"){inclusive = true}
                    } }
            )
        }
    }
}