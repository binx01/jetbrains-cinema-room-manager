package cinema

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class Cinema(private val rows: Int = 7, private val seatsPerRow: Int = 8) {

    private var room = emptyArray<CharArray>()

    init {
        for (r in 1..rows) {
            room += CharArray(seatsPerRow) { 'S' }
        }
    }

    private val seatsTotal = rows * seatsPerRow

    private val seatsTaken: Array<Array<Int>>
        get() {
            var seats = emptyArray<Array<Int>>()
            room.forEachIndexed { index, chars ->
                if (chars.contains('B'))
                    chars.forEachIndexed { i, c ->
                        if (c == 'B') seats += arrayOf(index, i)
                    }
            }
            return seats
        }

    private val purchasedTickets: Int
        get() = seatsTaken.size

    private val purchasedTicketsPercentage: Double
        get() = seatsTaken.size.toDouble() / seatsTotal.toDouble() * 100.00

    private val totalIncome: Int
        get() = if (seatsTotal < 60) seatsTotal * 10 else rows / 2 * seatsPerRow * 10 + ((seatsTotal - (rows / 2 * seatsPerRow)) * 8)

    private val currentIncome: Int
        get() = seatsTaken.sumBy { getTicketPrice(it[0] + 1) }

    private fun takeSeat(row: Int, seat: Int) {
        if (room[row - 1][seat - 1] == 'B') throw Error("No free seat!")
        room[row - 1][seat - 1] = 'B'
    }

    private fun getTicketPrice(row: Int): Int {
        return if (seatsTotal < 60) 10 else if (row <= rows / 2) 10 else 8
    }

    fun showRoom(): String {
        val sb = StringBuilder()
        sb.append("Cinema:\n")
        for (seat in 0..seatsPerRow) sb.append(if (seat == 0) "\u0020\u0020" else "$seat ")
        sb.append("\n")
        for (index in room.indices) {
            sb.append("${index + 1} ")
            for (seat in room[index]) {
                sb.append("$seat ")
            }
            sb.append("\n")
        }
        return sb.toString()
    }

    fun buyTicket(row: Int, seat: Int): Int {
        takeSeat(row, seat)
        return getTicketPrice(row)
    }

    fun showStatistics(): String {
        val sb = StringBuilder()
        sb.append("Number of purchased tickets: $purchasedTickets\n")
        sb.append(
            "Percentage: ${
                DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH)).format(
                    purchasedTicketsPercentage
                )
            }%\n"
        )
        sb.append("Current income: $$currentIncome\n")
        sb.append("Total income: $$totalIncome\n")
        return sb.toString()
    }
}

class Menu(private val menuItems: List<MenuItem>) {

    class MenuItem(val id: Int, val title: String)

    private var selectedMenu: MenuItem? = null

    fun showMenu(): String {
        val sb = StringBuilder()
        for (item in menuItems) sb.append("${item.title}\n")
        return sb.toString()
    }

    fun select(menuId: Int): MenuItem? {
        selectedMenu = menuItems.find { it.id == menuId }
        return selectedMenu
    }
}

fun main() {

    println("Enter the number of rows:")
    val rows = readLine()!!.toInt()

    println("Enter the number of seats in each row:")
    val seats = readLine()!!.toInt()

    val cinema = Cinema(rows, seats)

    val menu = Menu(
        listOf(
            Menu.MenuItem(1, "1. Show the seats"),
            Menu.MenuItem(2, "2. Buy a ticket"),
            Menu.MenuItem(3, "3. Statistics"),
            Menu.MenuItem(0, "0. Exit")
        )
    )

    do {
        println(menu.showMenu())
        val menuId = menu.select(readLine()!!.toInt())?.id
        when (menuId) {
            0 -> println("Exiting...")
            1 -> {
                println(cinema.showRoom())
            }
            2 -> {
                var error: Boolean
                do {
                    println("Enter a row number:")
                    val row = readLine()!!.toInt()

                    println("Enter a seat number in that row:")
                    val seat = readLine()!!.toInt()

                    try {
                        println("Ticket price: $${cinema.buyTicket(row, seat)}")
                        error = false
                    } catch (e: ArrayIndexOutOfBoundsException) {
                        error = true
                        println("Wrong input!")
                    } catch (e: Error) {
                        error = true
                        println(if (e.message == "No free seat!") "That ticket has already been purchased!" else e.message)
                    }
                    println()

                } while (error)
            }
            3 -> {
                println(cinema.showStatistics())
            }
        }

    } while (menuId != 0)
}