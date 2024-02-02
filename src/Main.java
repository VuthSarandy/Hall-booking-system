
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    static int MAX_BOOKINGS = 100;
    static String[][] bookingHistory = new String[MAX_BOOKINGS][4];
    private static int bookingCount = 0;
    private static void initializeSeats(String[][] shift) {
        for (int i = 0; i < shift.length; i++) {
            for (int j = 0; j < shift[i].length; j++) {
                shift[i][j] = "|" + (char)('A' + i) + "-" + (j + 1) + "::AV|";
            }
        }
    }
    private static void bookSeats(String[][] shift, String hall) {
        Scanner scanner = new Scanner(System.in);
        String userInput = validateBooking(scanner, "==> Please select available seat: ");
        String[] seatRequests = userInput.split(",");
        boolean validInput = true;
        for (String request : seatRequests) {
            String[] seatInfo = request.trim().split("-");
            if (seatInfo.length != 2 || !isValidSeatRequest(seatInfo, shift)) {
                System.out.println("Invalid seat request: " + userInput);
                validInput = false;
                break;
            }
        }
        if (validInput) {
            System.out.print("Please Enter Student Id: ");
            String studentId = scanner.nextLine();
            String confirm = validateString(scanner, "Are you sure you want to book? (Y/N): ");
            if (confirm.equalsIgnoreCase("Y") || confirm.equalsIgnoreCase("YES")) {
                for (String request : seatRequests) {
                    String[] seatInfo = request.trim().split("-");
                    char row = seatInfo[0].charAt(0);
                    int col = Integer.parseInt(seatInfo[1]) - 1;
                    if (shift[row - 'A'][col].contains("AV")) {
                        shift[row - 'A'][col] = "|" + row + "-" + (col + 1) + "::BO|";
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String bookingTime = now.format(formatter);
                        String hallShift = hall.equalsIgnoreCase("A")?"Morning - Shift":hall.equalsIgnoreCase("B")?"Afternoon - Shift":hall.equalsIgnoreCase("C")?"Night - Shift":" ";
                        storeBooking(studentId, seatInfo[0] + "-" + seatInfo[1], bookingTime, hallShift);
                    } else {
                        System.out.println("Seat " + seatInfo[0] + "-" + seatInfo[1] + " is already booked!");
                    }
                }
            } else {
                System.out.println("Booking canceled!");
            }
            System.out.println("Seat " + userInput + " booked successfully for Student ID: " + studentId);
        }
    }
    private static void storeBooking(String studentId, String seat, String bookingTime, String hall) {
        for (int i = 0; i < bookingCount; i++) {
            if (bookingHistory[i][0] != null && bookingHistory[i][0].equals(studentId)) {
                bookingHistory[i][1] += ", " + seat;
                return;
            }
        }
        bookingHistory[bookingCount][0] = studentId;
        bookingHistory[bookingCount][1] = seat;
        bookingHistory[bookingCount][2] = bookingTime;
        bookingHistory[bookingCount][3] = hall;
        bookingCount++;
    }

    private static void bookingHistory() {
        System.out.println(" +"+"=".repeat(43)+"+");
        System.out.println("|     >> Booking History <<"+" ".repeat(19)+"|");
        System.out.println(" +"+"=".repeat(43)+"+");
        if(bookingCount < 1){
            System.out.println("|        There is no history!"+" ".repeat(17)+"|");
        }else {
            for (int i = 0; i < bookingCount; i++) {
                System.out.println("|     #HALL: " + bookingHistory[i][3]);
                System.out.println("|     #NO: " + (i+1));
                System.out.println("|     #STUDENT ID: " + bookingHistory[i][0]);
                System.out.println("|     #SEATS: " + bookingHistory[i][1]);
                System.out.println("|     #BOOKING TIME: " + bookingHistory[i][2]);
                System.out.println(" +"+"=".repeat(43)+"+");
            }
        }
    }
    private static void rebootHistory(Scanner scanner, String[][] morning, String[][] afternoon, String[][] night) {
        String confirm = "";
        confirm = validateString(scanner, "Are you sure you want to reboot(Y/N)?: ");
        if (confirm.equalsIgnoreCase("Y") || confirm.equalsIgnoreCase("YES")) {
            bookingHistory = new String[MAX_BOOKINGS][4];
            bookingCount = 0;
            for (int i = 0; i < morning.length; i++) {
                for (int j = 0; j < morning[i].length; j++) {
                    morning[i][j] = "|" + (char) ('A' + i) + "-" + (j + 1) + "::AV|";
                    afternoon[i][j] = "|" + (char) ('A' + i) + "-" + (j + 1) + "::AV|";
                    night[i][j] = "|" + (char) ('A' + i) + "-" + (j + 1) + "::AV|";
                }
            }
            System.out.println("All seats rebooted successfully...!");
        }else{
            System.out.println("Canceled reboot!");
        }

    }
    private static boolean isValidSeatRequest(String[] seatInfo, String[][] shift) {
        if (seatInfo.length == 2) {
            char row = seatInfo[0].charAt(0);
            int col = Integer.parseInt(seatInfo[1]) - 1;
            return (row >= 'A' && row < 'A' + shift.length && col >= 0 && col < shift[0].length &&
                    shift[row - 'A'][col].contains("AV") && !shift[row - 'A'][col].contains("BO"));
        }
        return false;
    }
    private static void displaySeats(String[][] seats, String desc) {
        System.out.println("=".repeat(60));
        System.out.printf("\t\t\t\t>> %s <<\t\t\t\t\n", desc);
        for (String[] row : seats) {
            for (String seat : row) {
                System.out.print(seat + "\t");
            }
            System.out.println();
        }
    }
    private static int validateInteger(Scanner scanner, String text) {
        int userInput = 0;
        boolean isValid = false;
        while (!isValid) {
            System.out.print(text);
            String input = scanner.nextLine();
            Pattern pattern = Pattern.compile("-?\\d+");
            if(pattern.matcher(input).matches()){
                userInput = Integer.parseInt(input);
                isValid = true;
            } else {
                System.out.println("Invalid input. Please input number only!");
            }
        }
        return userInput;
    }
    private static String validateString(Scanner scanner, String text) {
        String validString = "";
        boolean isValid = false;
        while (!isValid) {
            System.out.print(text);
            String input = scanner.nextLine();
            Pattern pattern = Pattern.compile("[a-zA-Z]+");
            if(pattern.matcher(input).matches()){
                validString = input;
                isValid = true;
            } else {
                System.out.println("Invalid input. Please input text only!");
            }
        }
        return validString;
    }
    private static String validateBooking(Scanner scanner, String text) {
        String validString = "";
        boolean isValid = false;
        while (!isValid) {
            System.out.print(text);
            String input = scanner.nextLine();
            Pattern pattern = Pattern.compile("^[A-Z]-\\d+(,[A-Z]-\\d+)*$");
            if(pattern.matcher(input).matches()){
                validString = input;
                isValid = true;
            } else {
                System.out.println("Invalid input. Please input (A-1 or A-1,B-2)!");
            }
        }
        return validString;
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SystemBooking.logo();
        int row = validateInteger(scanner,"==> Config total rows in hall: ");
        int seat = validateInteger(scanner, "==> Config total seats per row in hall: ");
        String[][] morningShift = new String[row][seat];
        String[][] afternoonShift = new String[row][seat];
        String[][] nightShift = new String[row][seat];

        initializeSeats(morningShift);
        initializeSeats(afternoonShift);
        initializeSeats(nightShift);
        do {
            SystemBooking.applicationMenu();
            String option = validateString(scanner, "==> Please select menu no: ");
            switch (option.toLowerCase()){
                case "a" -> {
                    SystemBooking.booking();
                    String shift = validateString(scanner, "==> Please select show time (A | B | C): ");
                    switch (shift.toUpperCase()){
                        case "A" -> {
                            displaySeats(morningShift,"Hall - Morning Shift");
                            SystemBooking.instruction();
                            bookSeats(morningShift, "A");
                        }
                        case "B" -> {
                            displaySeats(afternoonShift,"Hall - Afternoon Shift");
                            SystemBooking.instruction();
                            bookSeats(afternoonShift, "B");
                        }
                        case "C" -> {
                            displaySeats(nightShift,"Hall - Night Shift");
                            SystemBooking.instruction();
                            bookSeats(nightShift, "C");
                        }
                        default -> {
                            System.out.println("Invalided option. Please choose option above!");
                        }
                    }
                }
                case "b" -> {
                    System.out.println("=".repeat(60));
                    System.out.println(" ".repeat(20)+"Hall Information");
                    displaySeats(morningShift,"Hall - Morning Shift");
                    displaySeats(afternoonShift,"Hall - Afternoon Shift");
                    displaySeats(nightShift,"Hall - Night Shift");
                    System.out.println("=".repeat(60));
                }
                case "c" -> {
                    SystemBooking.showTime();
                }
                case "d" -> {
                    rebootHistory(scanner, morningShift, afternoonShift, nightShift);
                }
                case "e" -> {
                    bookingHistory();
                }
                case "f" -> {
                    System.exit(0);
                }
                default -> {
                    System.out.println("Invalided option. Please choose option above!");
                }
            }
        }while (true);
    }
}
