/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package currdig.gui;

import blockchain.utils.BlockChain;
import blockchain.utils.Miner;
import p2p.P2Plistener;

/**
 *
 * @author vasco
 */
public class MiningApp {

    public static void main(String[] args) {
        // Create and initialize listener for mining events
        P2Plistener listener = new P2Plistener() {
            @Override
            public void onStartMining(String message, int zeros) {
                System.out.println("Mining started: " + message);
            }

            @Override
            public void onStopMining(String message, int nonce) {
                System.out.println("Mining stopped: " + message + " Nonce: " + nonce);
            }

            @Override
            public void onNounceFound(String threadName, int nonce) {
                System.out.println("Nonce found by " + threadName + ": " + nonce);
            }

            @Override
            public void onException(Exception e, String context) {
                e.printStackTrace();
            }

            @Override
            public void onStart(String message) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void onConect(String address) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void onTransaction(String transaction) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void onMessage(String title, String message) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void onStartRemote(String message) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void onBlockchainUpdate(BlockChain b) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        };

        // Create a Miner instance and start mining
        Miner miner = new Miner(listener);

        try {
            String message = "blockchain mining example";
            int zeros = 4; // We want the hash to start with 4 zeros
            miner.startMining(message, zeros); // Start the mining process

            // Wait for mining to finish and get the nonce
            int nonce = miner.waitToNonce(); // This will block until mining completes

            System.out.println("Mining complete, nonce: " + nonce);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
