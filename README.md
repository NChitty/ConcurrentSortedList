# Assignment 3
## Introduction
This project covers key areas of parallel and distributed computing. Part 1 is 
essentially asking to implement a lock-free linked list, as described in chapter 9,
and use is to solve the gift program. Part 2 is all about synchronization and allowing
threads to do work on objects and synchronize all the data at the end while allowing the
threads to do more work.
## Part 1 - The Gift Problem
The issue with the way the minotaur tried to implement his servants when writing thank yous
originally was that it was possible that a remove and add operation occured on consecutive nodes
in the chain which resulted in the removed node remaining in the list and not enough thank yous
being written. 
### The Implementation
Following the pseudocode given in Chapter 9 of the book for the lock-free linked list, everything
went very smoothly.
### Problems
The only issue I encountered while doing this was trying to not use sentinel nodes `head` and `tail`,
which I had to change. I also added `compareTo` in the `Node<T>` class in order to ensure that tail was
always last and head is always first, always allowing a placement between the two nodes.
## Part 2 - Thermometer
This part is very simple, we need 8 threads to read temperature sensors, report their data, and have it compiled.
### The Implementation
I created a thermometer data object that did all the calculations for a thread. The thread would return a `Future` that
at the end of an hour, any single thread could compose all of the data from the `Future`s. The thermometer data generates 
60 random numbers (1 per minute) to simulate this task.
### Problems
Most of the problems I had were with general logic of making sure the thermometer acted properly. This is was solved relatively easily.

## Setup and Running
First and foremost, make sure that you have the JDK installed as well as a JRE.

If you are on Windows you can clone this repo with:

```git clone https://github.com/NChitty/ConcurrentSortedList.git```

Once this is done, just run the `build.bat` file in the home directory, then move on to running the program.

If you are on Linux or Mac, you can use `build.sh` instead.

This program has some arguments involved, in general the syntax for running the program is 
`java -jar build\Project.jar [part] [seed]`.