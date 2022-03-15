package com.example.tiktaktoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiktaktoe.ui.theme.TikTakToeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TikTakToeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    val input1 = remember { mutableStateOf(0) }
    val input2 = remember { mutableStateOf(0) }
    val input3 = remember { mutableStateOf(0) }
    val input4 = remember { mutableStateOf(0) }
    val input5 = remember { mutableStateOf(0) }
    val input6 = remember { mutableStateOf(0) }
    val input7 = remember { mutableStateOf(0) }
    val input8 = remember { mutableStateOf(0) }
    val input9 = remember { mutableStateOf(0) }
    val winner = remember { mutableStateOf(0) }
    val xWins = remember { mutableStateOf(0) }
    val oWins = remember { mutableStateOf(0) }

    val showScore = remember { mutableStateOf(false) }
    val XorO = remember { mutableStateOf(true) }
    val added = remember { mutableStateOf(true) }
    val array = listOf<MutableState<Int>>(input1,input2,input3,input4,input5,input6,input7,input8,input9)

    val middle = 100
    val fonty = 40.sp
    val buttonMod = Modifier.size(width = 100.dp, height = 100.dp)
    Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top =(middle/2).dp)) {

        Text("Tic Tac Toe", fontSize = 25.sp)
        Text("(demo)")
        Row(modifier = Modifier.padding(top = (middle/2).dp)) {
            Button(onClick = {xo(input1,XorO)}, modifier = buttonMod) {
                Text(numbToLetter(input1),fontSize = fonty)
            }
            Button(onClick = {xo(input2,XorO)}, modifier = buttonMod) {
                Text(numbToLetter(input2),fontSize = fonty)
            }
            Button(onClick = {xo(input3,XorO)}, modifier = buttonMod) {
                Text(numbToLetter(input3),fontSize = fonty)
            }
        }
        Row() {
            Button(onClick = { xo(input4,XorO)}, modifier = buttonMod) {
                Text(numbToLetter(input4),fontSize = fonty)
            }
            Button(onClick = {xo(input5,XorO)}, modifier = buttonMod) {
                Text(numbToLetter(input5),fontSize = fonty)
            }
            Button(onClick = {xo(input6,XorO)}, modifier = buttonMod) {
                Text(numbToLetter(input6),fontSize = fonty)
            }
        }
        Row(modifier = Modifier.padding(bottom = 30.dp)) {
            Button(onClick = {xo(input7,XorO)}, modifier = buttonMod) {
                Text(numbToLetter(input7),fontSize = fonty)
            }
            Button(onClick = { xo(input8,XorO)}, modifier = buttonMod) {
                Text(numbToLetter(input8),fontSize = fonty)
            }
            Button(onClick = {xo(input9,XorO)}, modifier = buttonMod) {
                Text(numbToLetter(input9),fontSize = fonty)
            }
        }
        if(winner.value != 0)
        {
            showScore.value = true
            Button(onClick = { clear(array,winner,XorO,added, winner, xWins, oWins)}) {
                Text("Clear")
            }
            Text("Winner: ".plus(xo(winner.value)))
        }
        if(showScore.value)
        {
            Text("X wins: ".plus(xWins.value))
            Text("O wins: ".plus(oWins.value))
        }
    }
    winner(array,winner)
}
fun numbToLetter(input: MutableState<Int>): String {
    return if(input.value == 1)
        "X"
    else if(input.value == 2)
        "O"
    else
        " "
}
fun checkWinner(winner:MutableState<Int>,xWins:MutableState<Int>,oWins:MutableState<Int>)
{
        if (winner.value == 1)
            xWins.value++
        if (winner.value == 2)
            oWins.value++
    winner.value = 0

}
fun xo(input: MutableState<Int>, XorO:MutableState<Boolean>){
    if(input.value != 1 && input.value != 2) {
        if (XorO.value) {
            input.value = 1
            XorO.value = false
        } else {
            input.value = 2
            XorO.value = true
        }
    }
}
fun xo(input: Int): String {

    if(input == 1) {
        return "X"
    }
    return if(input == 2){

        "O"
    }
    else
        ""
}
fun clear(board: List<MutableState<Int>>, winState: MutableState<Int>,
          XorO: MutableState<Boolean>,added: MutableState<Boolean>,
          winner:MutableState<Int>,xWins:MutableState<Int>,oWins:MutableState<Int>)
{
    checkWinner(winner,xWins,oWins)
    added.value = false
    for (input in board)
    {
        input.value = 0
    }
    winState.value = 0
    XorO.value = true
}
fun winner(board:List<MutableState<Int>>, winState:MutableState<Int>)
{
    val winCon = listOf<IntArray>(
        intArrayOf(0,1,2),
        intArrayOf(1,4,7),
        intArrayOf(2,5,8),
        intArrayOf(0,3,6),
        intArrayOf(3,4,5),
        intArrayOf(6,7,8),
        intArrayOf(0,4,8),
        intArrayOf(2,4,6)
    )
    for(i in winCon)
    {
        if(board[i[0]].value != 0 && board[i[0]].value == board[i[1]].value && board[i[1]].value == board[i[2]].value)
            winState.value = board[i[0]].value
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TikTakToeTheme {
        Greeting("Android")

    }
}