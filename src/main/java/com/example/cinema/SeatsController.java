package com.example.cinema;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;


@RestController
public class SeatsController {

    @GetMapping("/seats")
     public SeatData getSeatsData() {
        SeatData seatData = new SeatData();
        seatData.setRows(9);
        seatData.setColumns(9);

        List<Seat> seats = new ArrayList<>();
        for (int row = 1; row <= seatData.getRows(); row++) {
            for (int column = 1; column <= seatData.getColumns(); column++) {
                seats.add(new Seat(row, column));
            }
        }
        seatData.setSeats(seats);
        return seatData;
    }

    @PostMapping("/purchase")
    public ResponseEntity<SeatWithPrice> purchaseSeat(@RequestBody Seat seat) {
        int row = seat.getRow();
        int column = seat.getColumn();

        SeatWithPrice purchaseSeat = new SeatWithPrice(row, column, 10);
        
        return ResponseEntity.ok(purchaseSeat);
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

        public Seat(int row, int column) {
            this.row = row;
            this.column = column;
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
    }

    static class SeatWithPrice extends Seat {
        private int price = 0;

        SeatWithPrice(int row, int column, int price) {
            super(row, column);
            this.price = price;

        }

        public void setPrice (int price) {
            this.price = price;
        }

        public int getPrice() {
            return this.price;
        }
    }

}
