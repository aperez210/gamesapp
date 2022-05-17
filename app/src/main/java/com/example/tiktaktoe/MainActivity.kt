package com.example.tiktaktoe

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.outlinedButtonColors
import androidx.compose.material.ButtonDefaults.textButtonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tiktaktoe.ui.theme.*
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import androidx.core.content.ContextCompat.getSystemService

open class Screen(val route:String){
    object  GameHome: Screen("list_screen")
    object  TicTacToe: Screen("tictactoe")
    object  Sudoku: Screen("sudoku")
    object  VictoryScreen: Screen("victory")
}
fun String.toLevelList():MutableList<Int>{
    var x = mutableListOf<Int>()
    this.forEachIndexed() { i, c ->
        if (c.isDigit()) {
            x.add(c.digitToInt())
        } else {

        }
    }
        return x
}
class Helper(context: Context?):SQLiteOpenHelper(context,"sudoku",null,1){
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE LEVELS(_id INTEGER PRIMARY KEY AUTOINCREMENT,LEVEL TEXT)")
        db?.execSQL("INSERT INTO LEVELS(LEVEL) VALUES"+
        "(\"[0,0,0,2,0,9,7,0,1,6,8,0,0,7,0,0,9,3,1,9,0,0,0,4,5,0,2,8,2,0,1,0,0,0,4,7,0,0,4,6,0,2,9,0,5,0,5,0,0,0,3,0,2,8,0,0,9,3,0,0,0,7,4,0,4,0,0,5,0,0,3,6,7,0,3,0,1,8,0,0,9]\")")
        db?.execSQL("INSERT INTO LEVELS(LEVEL) VALUES"+
        "(\"[0,0,5,8,0,1,7,3,2,3,0,0,0,4,6,1,8,9,9,0,0,3,0,7,4,5,6,2,1,3,0,0,0,8,9,0,5,4,0,1,0,9,6,0,0,7,9,6,0,0,8,0,0,3,1,0,0,0,7,5,9,0,8,0,7,9,6,1,0,5,4,3,0,5,0,9,8,3,0,0,1]\")")
        db?.execSQL("INSERT INTO LEVELS(LEVEL) VALUES"+
        "(\"[0,0,6,0,0,9,1,8,3,0,9,0,6,5,0,0,7,4,4,0,0,0,7,0,6,0,0,3,0,0,2,8,5,4,6,0,0,1,0,7,0,0,0,0,8,0,8,0,0,4,0,7,0,9,1,0,8,5,0,7,0,0,0,0,0,0,0,0,0,0,0,0,0,3,2,0,0,4,8,1,7]\")")

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            textButtonColors()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val id = "note"
                val name = "channel"
                val descriptionText = "alex's channel"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(id,name,importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                //val noteMan = NotificationManager().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                //noteMan.createNotificationChannel(channel)
            var builder = NotificationCompat.Builder(this,"channel")
            val br: BroadcastReceiver = MyBroadcastReceiver()
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
                addAction(Intent.ACTION_TIME_TICK)
            }
            registerReceiver(br, filter)
            val level =  remember { mutableStateOf(0) }
            val navController = rememberNavController()
            NavHost(navController = navController,
            startDestination =  Screen.GameHome.route){
            composable(route = Screen.GameHome.route)   {   GameHome(navController = navController)}
            composable(route = Screen.TicTacToe.route)  {   TicTacToe(navController = navController)}
            composable(route = Screen.Sudoku.route)  {   Sudoku(level,navController = navController,applicationContext) }
            composable(route = Screen.VictoryScreen.route)  {   VictoryScreen(navController = navController) }
            }
        }
    }
}


class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        StringBuilder().apply {
            append("Action: ${intent.action}\n")
            append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
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
        floatingActionButtonPosition = FabPosition.Center,
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
                    Card(shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()){
                        Text("Please select a game",
                            fontSize = 25.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        onClick = {navController.navigate(route = Screen.TicTacToe.route) }) {
                        Text("TicTacToe")
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navController.navigate(route = Screen.Sudoku.route)}) {
                        Text("Sudoku" )
                    }
    }) {
        Column(

            Modifier
                .fillMaxSize()
                .padding(65.dp)
                .background(windrunner,Shapes().large),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.titletextfill),
                contentDescription = "Title Text",
                Modifier.fillMaxWidth()
            )

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

    val dialogVisibility = remember { mutableStateOf(false)}
    val middle = 100
    val fonty = 40.sp
    val buttonMod = Modifier.size(width = 100.dp, height = 100.dp)
    val scaffoldState = rememberScaffoldState()
    Scaffold (
        topBar = {
                 Card(modifier = Modifier
                     .fillMaxWidth()
                     .height(55.dp)
                     .background(color = lightweaver, shape = RoundedCornerShape(10.dp))){
                     Text("Tic Tac Toe", fontSize = 25.sp)
                 }
        },
        scaffoldState = scaffoldState,
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { /*TODO*/ },
                onClick = {navController.navigate(Screen.GameHome.route)})},
        modifier =  Modifier.background(color = lightweaver,shape = RoundedCornerShape(10.dp))
        ){
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top =(middle/2).dp)) {
            Row(modifier = Modifier.padding(start = 50.dp,top = (middle/2).dp)) {
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
            Row(Modifier.padding(start = 50.dp)) {
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
            Row(modifier = Modifier.padding(start = 50.dp, bottom = 30.dp)) {
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
                Text("Winner: ".plus(xo(winner.value)))
                dialogVisibility.value = true
            }
            if(showScore.value)
            {
                Row(){
                    Column() {
                        Text("X")
                        Text(xWins.value.toString())
                    }
                    Column() {
                        Text("O")
                        Text(oWins.value.toString())
                    }
                }

            }
            if (dialogVisibility.value){
                Dialogue(type = DialogueType.INFO,
                    title = xo(winner.value) + "\'s win",
                    desc = "",
                    onDismiss ={
                        clear(array,winner,XorO,added, winner, xWins, oWins);
                        dialogVisibility.value = false})
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
    if(this.size == 81) {
        boxes.value = this
    }else if (this.size > 81){
        boxes.value = this.subList(0,80)
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
fun Dialogue(
    type: DialogueType,
    title: String,
    desc: String,
    onDismiss: () -> Unit
) {
    MaterialTheme {
        when (type) {
            DialogueType.SUCCESS -> {
                SuccessDialog(
                    title = title,
                    desc = desc,
                    onDismiss = onDismiss
                )
            }
            DialogueType.ERROR -> {
                ErrorDialog(
                    title = title,
                    desc = desc,
                    onDismiss = onDismiss
                )
            }
            DialogueType.INFO -> {
                InfoDialog(
                    title = title,
                    desc = desc,
                    onDismiss = onDismiss
                )
            }
        }
    }
}

enum class  DialogueType{
    SUCCESS, ERROR, INFO
}

@Composable
fun SuccessDialog(
    title: String,
    desc: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .width(300.dp)
                .height(400.dp)
        ) {
            Column(
                modifier = Modifier
                    .size(300.dp)
            ) {
                Spacer(modifier = Modifier.height(36.dp))
                Box(
                    modifier = Modifier
                        .width(300.dp)
                        .height(164.dp)
                        .background(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.DarkGray
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = title.uppercase(),
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = desc,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            )
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = onDismiss,
                                shape = Shapes().large,
                                colors = ButtonDefaults.buttonColors(windrunner),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(5.dp))
                            ) {
                                Text(
                                    text = "Cancel"
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = onDismiss,
                                shape = Shapes().large,
                                colors = ButtonDefaults.buttonColors(windrunner),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(5.dp))
                            ) {
                                Text(
                                    text = "Ok",
                                    color = kholin_blue
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ErrorDialog(
    title: String,
    desc: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .width(300.dp)
                .height(400.dp)
        ) {
            Column(
                modifier = Modifier
                    .size(300.dp)
            ) {
                Spacer(modifier = Modifier.height(36.dp))
                Box(
                    modifier = Modifier
                        .width(300.dp)
                        .height(164.dp),

                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = title.uppercase(),
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = desc,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            )
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = onDismiss,
                                shape = Shapes().large,
                                colors = textButtonColors(dustbringer),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(5.dp))
                            ) {
                                Text(
                                    text = "Cancel",
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = onDismiss,
                                shape = Shapes().large,
                                colors = ButtonDefaults.buttonColors(backgroundColor = windrunner),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(5.dp))
                            ) {
                                Text(
                                    text = "Ok",
                                    color = kholin_blue
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoDialog(
    title: String,
    desc: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .width(300.dp)
                .height(400.dp)
        ) {
            Column(
                modifier = Modifier
                    .size(300.dp)
            ) {
                Spacer(modifier = Modifier.height(36.dp))
                Box(
                    modifier = Modifier
                        .width(300.dp)
                        .height(164.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = title.uppercase(),
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = desc,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            )
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onDismiss,
                            shape = Shapes().large,
                            colors = ButtonDefaults.buttonColors(backgroundColor = kholin_blue),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(5.dp))
                        ) {
                            Text(
                                text = "Ok"
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun CustomDialog() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val successDialog = remember { mutableStateOf(false) }
        val errorDialog = remember { mutableStateOf(false) }
        val infoDialog = remember { mutableStateOf(false) }

        Button(
            onClick = { successDialog.value = true },
            colors = ButtonDefaults.buttonColors(backgroundColor = windrunner),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .clip(RoundedCornerShape(5.dp))
        ) {
            Text(
                text = "Success Dialog",
                color = lightweaver,
                modifier = Modifier
                    .padding(vertical = 8.dp),
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = { errorDialog.value = true },
            colors = ButtonDefaults.buttonColors(backgroundColor = dustbringer),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .clip(RoundedCornerShape(5.dp))
        ) {
            Text(
                text = "Error Dialog",
                color = lightweaver,
                modifier = Modifier
                    .padding(vertical = 8.dp),
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = { infoDialog.value = true },
            colors = ButtonDefaults.buttonColors(backgroundColor = bondsmith),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .clip(RoundedCornerShape(5.dp))
        ) {
            Text(
                text = "Info Dialog",
                color = lightweaver,
                modifier = Modifier
                    .padding(vertical = 8.dp),
                fontSize = 16.sp
            )
        }

        if (successDialog.value) {
            Dialogue(
                type = DialogueType.SUCCESS,
                title = "Success",
                desc = "This is a Success Dialog",
                onDismiss = {
                    successDialog.value = false
                }
            )
        }

        if (errorDialog.value) {
            Dialogue(
                type = DialogueType.ERROR,
                title = "Error",
                desc = "This is a Error Dialog",
                onDismiss = {
                    errorDialog.value = false
                }
            )
        }

        if (infoDialog.value) {
            Dialogue(
                type = DialogueType.INFO,
                title = "Info",
                desc = "This is a Info Dialog",
                onDismiss = {
                    infoDialog.value = false
                }
            )
        }
    }
}

@Composable
fun Sudoku(level: MutableState<Int>, navController: NavHostController, ac: Context) {
    var i = 0
    val boxes = remember{mutableStateOf(mutableListOf<Int>(81))}
    val levels = LevelList(ac)

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
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
        FloatingActionButton(onClick = { navController.navigate(Screen.GameHome.route) }) {
            Text("<-")
        }
    }) {
        LazyColumn(
            content = {
                item {
                    val x = level.value
                    Text("Sudoku \n level: " + (x+1).toString(),
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
                    val seeDialogue = remember { mutableStateOf(false) }
                    Button(
                        onClick = {
                            if (boxes.value.compare(levelSolution)) {
                                navController.navigate(Screen.VictoryScreen.route)
                                level.value++
                                levels[level.value].publishTo(boxes)
                            } else {
                               seeDialogue.value = true
                            }
                        },
                    ) {
                        Text("Check")
                    }
                    if (seeDialogue.value){
                        Dialogue(type = DialogueType.INFO,
                            title = "Check Result:",
                            desc = "Your answer is incorrect!",
                            onDismiss ={seeDialogue.value = false})
                    }
                    /*
                    Button(onClick = {
                        boxes.value = levelSolution
                    }) {
                        Text("Complete")
                    }*/
                }
            }
            })
        }
    println("[**]" + levels[level.value])
    levels[level.value].publishTo(boxes)
    println("[**]" +levels[level.value])
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
            val seeDialogue = remember{ mutableStateOf(true)}
            if (seeDialogue.value){
                Dialogue(type = DialogueType.INFO,
                    title = "Level Result:",
                    desc = "Congratulations you beat a level",
                    onDismiss ={seeDialogue.value = false})
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
    Button( onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(windrunner),
            modifier = Modifier.size(squButtSize,squButtSize)) {
            if(boxes.value[number] == 0){

            } else {
                Text(boxes.value[number].toString())
            }
    }
    DropdownMenu(   expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .width(50.dp)
                        .background(lightweaver)
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
fun LevelList(ac: Context): MutableList<MutableList<Int>> {
    var x = mutableListOf<MutableList<Int>>()
    var helper = Helper(ac)
    var db = helper.readableDatabase
    var rs = db.rawQuery("SELECT * FROM LEVELS",null)
    var rz = db.rawQuery("SELECT COUNT(*) FROM LEVELS",null)
    var size = 0
    if(rz.moveToFirst()){
        size = rz.getInt(0)
    }
    if (rs.moveToFirst()){
        x.add(rs.getString(1).toLevelList())
        repeat(size - 1){
            if (rs.moveToNext()){
                x.add(rs.getString(1).toLevelList())
            }
        }
    }
    return x
}}