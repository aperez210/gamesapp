package com.example.tiktaktoe

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.fonts.Font
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import java.net.URI.create


open class Screen(val route:String){
    object  GameHome: Screen("list_screen")
    object  TicTacToe: Screen("tictactoe")
    object  Sudoku: Screen("sudoku")
    object  VictoryScreen: Screen("victory")
}
val goodDog = Font(R.font.gooddog)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val level =  remember { mutableStateOf(0) }
            val navController = rememberNavController()
            NavHost(navController = navController,
            startDestination =  Screen.GameHome.route){
            composable(route = Screen.GameHome.route)   {   GameHome(navController = navController)}
            composable(route = Screen.TicTacToe.route)  {   TicTacToe(navController = navController)}
            composable(route = Screen.Sudoku.route)  {   Sudoku(level,navController = navController) }
            composable(route = Screen.VictoryScreen.route)  {   VictoryScreen(navController = navController) }
            }


        }
    }
}


//Screen 1
@Composable
fun GameHome(navController: NavController){
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()


    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            ExtendedFloatingActionButton(

                text = { /*TODO*/ },
                onClick = {
                    scope.launch {
                        scaffoldState.drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                }) },
                drawerContent = {
                    Button(onClick = {navController.navigate(route = Screen.TicTacToe.route) }) {
                        Text("TicTacToe")
                    }
                    Button(onClick = { navController.navigate(route = Screen.Sudoku.route) }) {
                        Text("Sudoku" )
                    }
    }) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(65.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Alex Perez's \n Assorted Games App",)

        }
    }
}

//Screen 2
@Composable
fun TicTacToe(navController: NavController) {
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
    val scaffoldState = rememberScaffoldState()
    Scaffold (
        scaffoldState = scaffoldState,
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { /*TODO*/ },
                onClick = {navController.navigate(Screen.GameHome.route)})},
        ){
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
                    Text(numbToLetter(input7), fontSize = fonty)
                }
                Button(onClick = { xo(input8,XorO)}, modifier = buttonMod) {
                    Text(numbToLetter(input8),fontSize = fonty)
                }
                Button(onClick = {xo(input9,XorO)}, modifier = buttonMod) {
                    Text(numbToLetter(input9),fontSize = fonty)
                }
            }
            if(winner.value != 0) {
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

//TTT Functions
fun checkWinner(winner:MutableState<Int>,xWins:MutableState<Int>,oWins:MutableState<Int>) {
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
    return if(input == 1)
        "X"
    else if(input == 2)
        "O"
    else
        ""
}
fun clear(board: List<MutableState<Int>>, winState: MutableState<Int>,
          XorO: MutableState<Boolean>,added: MutableState<Boolean>,
          winner:MutableState<Int>,xWins:MutableState<Int>,oWins:MutableState<Int>) {
    checkWinner(winner,xWins,oWins)
    added.value = false
    for (input in board)
    {
        input.value = 0
    }
    winState.value = 0
    XorO.value = true
}
fun winner(board:List<MutableState<Int>>, winState:MutableState<Int>) {
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

fun MutableList<Int>.publishTo(boxes: MutableState<MutableList<Int>>){
    if(this.size == 81)
    {
        boxes.value = this
    }
}
fun MutableList<Int>.compare(list:MutableList<Int>): Boolean {
    if (this.size == list.size) {
        if (this.equals(list))
            return true
    }
    return false
}
@Composable
fun TheDialog(
    title:String,
    desc:String,
    onDismiss: () -> Unit){
    Dialog(
        onDismissRequest = onDismiss
    ){
        Box(
            modifier = Modifier.width(300.dp).height(400.dp)
        ){

        }
    }
}

@Composable
fun Sudoku(level:MutableState<Int>,navController: NavHostController) {
    var i = 0
    val boxes = remember{mutableStateOf(mutableListOf<Int>(81))}
    val levels = LevelList()


    var solutionStr = ""
    runBlocking {
        val job = launch(Dispatchers.Default) {
            try {
                val answer = getSolution(callSudokuAPI(LeveltoJson(levels[level.value])))

                if(!answer.isNullOrBlank())
                    solutionStr = answer
            } catch (e: Exception) {
                System.out.println(e)
            }
        }
    }
    val levelSolution = solutionStr.toIntMutableList()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
        FloatingActionButton(onClick = { navController.navigate(Screen.GameHome.route) }) {
            Text("<-")
        }
    }) {
        LazyColumn(
            content = {
                item {
                    Text("Sudoku \n level: " + level.value+1,
                        modifier = Modifier.padding(bottom = 25.dp, top = 25.dp,start = 25.dp))}
                    repeat(9) {
                    item{
                        LazyRow(
                            Modifier
                                .padding(start = 15.dp)
                                .border(BorderStroke(3.dp, Color.Black)),
                            content = {
                            repeat(9) {
                                item {i++;SelectionSquare(i, boxes) }
                            }
                        })
                    }
                }
            item {
                Row(
                    Modifier.padding(top = 25.dp)
                ){

                    Button(
                        onClick = {
                            //TODO Button clicky
                            System.out.println(levelSolution)
                            if (boxes.value.compare(levelSolution)) {
                                navController.navigate(Screen.VictoryScreen.route)
                                level.value++
                                levels[level.value].publishTo(boxes)
                            } else {
                                //TODO DIALOG
                            }
                        },
                    ) {
                        Text("Check")
                    }
                    Button(onClick = {boxes.value = levelSolution}) {
                        Text("Complete")
                    }
                }
            }
            })
        }
    levels[level.value].publishTo(boxes)
}
fun callSudokuAPI(level:String): String? {
    val client = OkHttpClient()
    val mediaType = MediaType.parse("application/json")
    val body = RequestBody.create(mediaType,level)
    val request = Request.Builder()
        .url("https://solve-sudoku.p.rapidapi.com/")
        .post(body)
        .addHeader("content-type", "application/json")
        .addHeader("X-RapidAPI-Host", "solve-sudoku.p.rapidapi.com")
        .addHeader("X-RapidAPI-Key", "de407b89c0mshcf56859620e2a02p152c77jsn9c0e5d6b263c")
        .build()
    val response = client.newCall(request).execute().body()?.string()
    return response
}
fun getSolution(S:String?): String? {
    val jsonElement: JsonElement = JsonParser().parse(S)
    val jsonObject = jsonElement.asJsonObject
    return jsonObject.get("solution").toString().substring(1,82)
}
fun String.toIntMutableList():MutableList<Int>{
    var listo = mutableListOf<Int>()
    this.forEachIndexed() {index, letter ->
        if (letter.isDigit()){
            listo.add(letter.digitToInt())
        }
    }
    return listo
}
@Composable
fun VictoryScreen(navController: NavHostController){
    LazyColumn(content = {
        item{
            Button(onClick = {navController.navigate(Screen.Sudoku.route)}) {
                Text("Go back")
            }
        }
    })
}
@Composable
fun SelectionSquare(boxNum:Int,boxes:MutableState<MutableList<Int>>){
    var expanded by remember { mutableStateOf(false) }
    val number by remember { mutableStateOf(boxNum-1)}
    val inputs = listOf<Int>(1,2,3,4,5,6,7,8,9,0)
    val squButtSize = 40.dp
    System.out.print(boxNum)
    System.out.println("\n")
    Button(onClick = {
        System.out.println("Button " + number + " pressed")
        expanded = true
    },  modifier = Modifier.size(squButtSize,squButtSize)) {
        if(boxes.value[number] != 0){
            Text(boxes.value[number].toString())
        } else {
            Text("_")
        }
    }
    DropdownMenu(   expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .width(50.dp)
                        .background(Color.White)
    )
    {
        inputs.forEachIndexed { index, s ->
            DropdownMenuItem(onClick = {
                expanded = false
                boxes.value.set(number,s)
                System.out.println(boxes.value[number].toString())
            })
            {
                Text(s.toString(), color = Color.Black)
            }
        }
    }
}
fun LeveltoJson(level:MutableList<Int>): String{
    var output = "{" + "\n" + "\"puzzle\": \""
    level.forEachIndexed { index, s ->
        if(level[s] == 0){
            output+="."
        }else{
            output+=s.toString()
        }
    }
    output+= "\"" + "\n" + "}"
    return output
}
fun LevelList(): List<MutableList<Int>> {
    //levels[0]
    val one = mutableListOf<Int>(
        0,0,0,2,0,9,7,0,1,
        6,8,0,0,7,0,0,9,3,
        1,9,0,0,0,4,5,0,2,
        8,2,0,1,0,0,0,4,7,
        0,0,4,6,0,2,9,0,5,
        0,5,0,0,0,3,0,2,8,
        0,0,9,3,0,0,0,7,4,
        0,4,0,0,5,0,0,3,6,
        7,0,3,0,1,8,0,0,9)
    val two = mutableListOf<Int>(
        0,0,5,8,0,1,7,3,2,
        3,0,0,0,4,6,1,8,9,
        9,0,0,3,0,7,4,5,6,
        2,1,3,0,0,0,8,9,0,
        5,4,0,1,0,9,6,0,0,
        7,9,6,0,0,8,0,0,3,
        1,0,0,0,7,5,9,0,8,
        0,7,9,6,1,0,5,4,3,
        0,5,0,9,8,3,0,0,1)
    val three = mutableListOf<Int>(
        0,0,6,0,0,9,1,8,3,
        0,9,0,6,5,0,0,7,4,
        4,0,0,0,7,0,6,0,0,
        3,0,0,2,8,5,4,6,0,
        0,1,0,7,0,0,0,0,8,
        0,8,0,0,4,0,7,0,9,
        1,0,8,5,0,7,0,0,0,
        0,0,0,0,0,0,0,0,0,
        0,3,2,0,0,4,8,1,7)

    return listOf(one,two,three)
}