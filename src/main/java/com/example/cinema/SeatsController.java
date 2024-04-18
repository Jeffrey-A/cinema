package com.example.cinema;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;


@RestController
public class SeatsController {
    List<Seat> purchasedSeats = new ArrayList<>();

    @GetMapping("/seats")
     public SeatData getSeatsData() {
        SeatData seatData = new SeatData();
        seatData.setRows(9);
        seatData.setColumns(9);

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

        if (row > 9 || column > 9) {
            ErrorResponse errorResponse = new ErrorResponse("The number of a row or a column is out of bounds!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        if (!this.isSeatAvailable(seat)) {
            ErrorResponse errorResponse = new ErrorResponse("The ticket has been already purchased!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        Seat purchaseSeat = new Seat(row, column, 10);
        purchasedSeats.add(purchaseSeat);
        
        return ResponseEntity.ok(purchaseSeat);
    }

    public boolean isSeatAvailable(Seat seat) {
        int row = seat.getRow();
        int column = seat.getColumn();

        for (Seat purchasedSeat: this.purchasedSeats) {
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
