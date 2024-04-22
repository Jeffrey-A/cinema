package com.example.cinema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Iterator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;


@RestController
public class SeatsController {
    int cinemaSize = 9;
    int totalSeats = cinemaSize * cinemaSize;

    List<Ticket> purchasedSeats = new ArrayList<>();

    @GetMapping("/seats")
     public SeatData getSeatsData() {
        SeatData seatData = new SeatData();
        seatData.setRows(cinemaSize);
        seatData.setColumns(cinemaSize);

        List<Seat> seats = new ArrayList<>();
        for (int row = 1; row <= seatData.getRows(); row++) {
            for (int column = 1; column <= seatData.getColumns(); column++) {
                int price = row <= 4 ? 10 : 8; 
                seats.add(new Seat(row, column, price));
            }
        }
        seatData.setSeats(seats);
        return seatData;
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseSeat(@RequestBody Seat seat) {
        int row = seat.getRow();
        int column = seat.getColumn();

        if (!this.isValidSeat(seat)) {
            ErrorResponse errorResponse = new ErrorResponse("The number of a row or a column is out of bounds!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        if (!this.isSeatAvailable(seat)) {
            ErrorResponse errorResponse = new ErrorResponse("The ticket has been already purchased!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        int price = row <= 4 ? 10 : 8;

        Seat purchaseSeat = new Seat(row, column, price);

        Ticket ticket = new Ticket(purchaseSeat);

        purchasedSeats.add(ticket);

        HashMap<String, Object> returnVal = new HashMap<>();
        returnVal.put("token", ticket.getToken());
        returnVal.put("ticket", ticket.getSeat());
        
        return ResponseEntity.ok(returnVal);
    }

    @PostMapping("/return")
    public ResponseEntity<?> returnTicket(@RequestBody Token token) {
        String tokenVal = token.getToken();
        HashMap<String, Object> returnVal = new HashMap<>();
        boolean isTokenValid = false;

        Iterator<Ticket> iterator = this.purchasedSeats.iterator();
        while (iterator.hasNext()) {
            Ticket ticket = iterator.next();
            String ticketToken = ticket.getToken();



            if (tokenVal.equals(ticketToken)) {
                returnVal.put("ticket", ticket.getSeat());
                isTokenValid = true;
                iterator.remove();
                break;
            }
        }

        if (isTokenValid) {
            return ResponseEntity.ok(returnVal);
        }

        ErrorResponse errorResponse = new ErrorResponse("Wrong token!");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@RequestParam(required = false, defaultValue = "") String password) {
        if (password.equals("super_secret")) {
            HashMap<String, Integer> returnVal = new HashMap<>();
            returnVal.put("income", this.getCurrentIncome());
            returnVal.put("available", this.totalSeats - purchasedSeats.size());
            returnVal.put("purchased", purchasedSeats.size());
            return ResponseEntity.ok(returnVal);
        }

        ErrorResponse errorResponse = new ErrorResponse("The password is wrong!");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    public int getCurrentIncome() {
        int income = 0;
        for(Ticket ticket: this.purchasedSeats) {
            Seat seat = ticket.getSeat();
            income += seat.getPrice();
        }
        return income;
    }

    public boolean isValidSeat(Seat seat) {
        int row = seat.getRow();
        int column = seat.getColumn();

        if (row >= 1 && row <= 9 && column >= 1 && column <= 9) {
            return true;
        }

        return false;
    }

    public boolean isSeatAvailable(Seat seat) {
        int row = seat.getRow();
        int column = seat.getColumn();

        for (Ticket ticket: this.purchasedSeats) {
            Seat purchasedSeat = ticket.getSeat();

            if (purchasedSeat.getRow() == row && purchasedSeat.getColumn() == column) {
                return false;
            }
        }
        return true;
    }
    
    static class SeatData {
        private int rows;
        private int columns;
        private List<Seat> seats;

        public int getRows() {
            return rows;
        }

        public void setRows(int rows) {
            this.rows = rows;
        }

        public int getColumns() {
            return columns;
        }

        public void setColumns(int columns) {
            this.columns = columns;
        }

        public List<Seat> getSeats() {
            return seats;
        }

        public void setSeats(List<Seat> seats) {
            this.seats = seats;
        }
    }

    static class Seat {
        private int row;
        private int column;
        private int price;

        public Seat(int row, int column, int price) {
            this.row = row;
            this.column = column;
            this.price = price;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getColumn() {
            return column;
        }

        public void setColumn(int column) {
            this.column = column;
        }

        public void setPrice (int price) {
            this.price = price;
        }

        public int getPrice() {
            return this.price;
        }
    }

    static class Ticket {
        private Token token;
        private Seat ticket;

        Ticket(Seat seat) {
            this.ticket = seat;
            this.token = new Token();
        }

        Seat getSeat() {
            return this.ticket;
        }

        public String getToken() {
            return this.token.getToken();
        }
    }

    static class Token {
        private String token;

        Token() {
            this.token = this.createToken();
        }

        public String createToken() {
            UUID uuid = UUID.randomUUID();
            return uuid.toString();
        }

        public String getToken() {
            return this.token;
        }
    }

    static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

}
