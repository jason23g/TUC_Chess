## Table of contents
* [Title](#title)
* [Description](#description)
* [Technologies](#technologies)
* [Setup](#setup)

## Title
AI agent for playing TUC-CHESS

## Description
This project has been implemented as part of an undergraduate course in AI (COMP 311).
The aim of the project was to develop an ΑΙ agent in order to play a variation of chess with less pieces(7 pawns,2 rooks,1 king each player) in a smaller Board(7x5).
The server and the client  code was given from the professor of the course, Georgios Chalkiadakis and two adversarial search algorithms (Minimax, MCTS) have been implemented in order client to play with other clients.

## Technologies
Project is created with:
* Eclipse IDE for Java Developers 2020-12
* jdk-15.02
* Libraries of java used :java.util (ArrayList, LinkedList, Random)


## Setup
To run this project, store the jar file locally named my_client.jar, run the jar file
tuc-chess-server.jar, then run my_client.jar with two arguments:
* The first argument is the delay of making a move, measured in milliseconds.
* The second argument is the adversarial search algorithm which the client will execute in order to decide its next move, 0 stands for random move, 1 stands for Minimax without using a-b pruning, 2 stands for Minimax using a-b pruning, 3 stands for Monte Carlo Tree search.

## Authors
* Manolis Perakis (AM : 2017030099)
* Jason Georgakas (AM : 2017030021)
