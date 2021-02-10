package com.company;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private String [] arrayCombinations = new String[]{};
    private int userMove;
    private String key;
    private void createKey(byte [] bytes){
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        key = String.format("%032X", new BigInteger(1, bytes));
    }

    private void printHMAC(String moveComputer, byte [] byteKey) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(byteKey, "HmacSHA256");
        sha256_HMAC.init(secretKeySpec);

        byte [] mac_data = sha256_HMAC.doFinal(moveComputer.getBytes(StandardCharsets.UTF_8));
        System.out.println("HMAC: " + String.format("%032X", new BigInteger(1, mac_data)));
    }

    private void printMenu(){
        System.out.println("Available moves:");
        for(int i = 0; i < arrayCombinations.length; i++){
            System.out.println(i + 1 + " - " + arrayCombinations[i]);
            if(i == arrayCombinations.length - 1)
                System.out.println(0 + " - Exit");
        }
    }

    private void inputValidation(){
        Scanner scan = new Scanner(System.in);
        userMove = scan.nextInt();
        if (userMove > arrayCombinations.length | userMove < 0) {
            System.out.println("Incorrect choice. Please make your choice from the options below:");
            printMenu();
            inputValidation();
        }
    }

    private void checkingMoves(String moveComputer){
        int step = (arrayCombinations.length - 1) / 2;

        if(arrayCombinations[userMove - 1].equals(moveComputer)){
            System.out.println("Draw.");
            startGame();
        }

        boolean win = true;
        if (userMove > step) {
            for (int i = userMove - 2; i >= userMove - step - 1; i--)
                if (arrayCombinations[i].equals(moveComputer)) {
                    System.out.println("You win!");
                    win = false;
                    break;
                }
            if(win)
                System.out.println("You lose!");
        }else {
            for (int i = userMove + 1; i <= userMove + step; i++)
                if (arrayCombinations[i].equals(moveComputer)) {
                    System.out.println("You lose!");
                    win = false;
                    break;
                }
            if (win)
                System.out.println("You win!");
        }
        System.out.println("HMAC key: " + key);
        startGame();
    }

    public void startGame() {
        try {
            createKey(new byte[16]);
            byte [] byteKey = key.getBytes(StandardCharsets.UTF_8);

            String moveComputer = arrayCombinations[new Random().nextInt(arrayCombinations.length)];

            printHMAC(moveComputer, byteKey);
            printMenu();
            inputValidation();

            if(userMove == 0)
                System.exit(0);

            System.out.println("Enter your move: " + userMove);
            System.out.println("Your move: " + arrayCombinations[userMove - 1]);
            System.out.println("Computer move: " + moveComputer);

            checkingMoves(moveComputer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setParameters(String [] array){

        for(int i = 0; i < array.length; i++)
            for(int j = i + 1; j < array.length; j++)
                if(array[i].equals(array[j])) {
                    System.out.println("The parameters you entered are incorrect. Enter an odd number of lines, for example: \"rock paper scissors\" or \"rock paper scissors lizard spock\".");
                    System.exit(0);
                }
        if (array.length % 2 != 0 & array.length >= 3)
            arrayCombinations = array;
        else {
            System.out.println("The parameters you entered are incorrect. Enter an odd number of lines, for example: \"rock paper scissors\" or \"rock paper scissors lizard spock\".");
            System.exit(0);
        }
    }

}
