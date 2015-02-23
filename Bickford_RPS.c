/* 
 * File:   Bickford_RPS.c
 * Author: David Bickford
 *
 * This is a Rock Paper Scissors program
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/*
 * 
 */
int main() 
{
    char userInput[15];
    char compChoice[15];
    int compScore = 0;
    int userScore = 0;
    int playCode = 0;
    int value = 0;
    srand((unsigned int)time(NULL));

    printf("Welcome to Rock, Paper, Scissors!\n\n");
    
    while(playCode == 0)
    {
        printf("Would you like to play? ");
        scanf("%s", &userInput);
        printf("\n");
        
        if(strncmp(userInput, "no", strlen(userInput)) == 0)
        {
            playCode = 1;
            break;
        }
        
        value = rand() % (3 - 1 + 1) + 1;
        strncpy(userInput, "", 15);
        strncpy(compChoice, "", 15);
        printf("What is your choice? ");
        scanf("%s", &userInput);
        
        if(value == 1)
        {
            strncpy(compChoice, "rock", 15);
            
            if(strncmp(userInput, "rock", strlen(userInput)) == 0)
            {
                printf("The computer chose rock. It's a tie!!\n\n");
            }
            else if(strncmp(userInput, "paper", strlen(userInput)) == 0)
            {
                printf("The computer chose rock. You win this game!\n\n");
                userScore++;
            }
            else if(strncmp(userInput, "scissors", strlen(userInput)) == 0)
            {
                printf("The computer chose rock. You lose this game!\n\n");
                compScore++;
            }
        }
        else if(value == 2)
        {
            strncpy(compChoice, "paper", 15);
            
            if(strncmp(userInput, "paper", strlen(userInput)) == 0)
            {
                printf("The computer chose paper. It's a tie!!\n\n");
            }
            else if(strncmp(userInput, "scissors", strlen(userInput)) == 0)
            {
                printf("The computer chose paper. You win this game!\n\n");
                userScore++;
            }
            else if(strncmp(userInput, "rock", strlen(userInput)) == 0)
            {
                printf("The computer chose paper. You lose this game!\n\n");
                compScore++;
            }
        }
        else if(value == 3)
        {
            strncpy(compChoice, "scissors", 15);
            
            if(strncmp(userInput, "scissors", strlen(userInput)) == 0)
            {
                printf("The computer chose scissors. It's a tie!!\n\n");
            }
            else if(strncmp(userInput, "rock", strlen(userInput)) == 0)
            {
                printf("The computer chose scissors. You win this game!\n\n");
                userScore++;
            }
            else if(strncmp(userInput, "paper", strlen(userInput)) == 0)
            {
                printf("The computer chose scissors. You lose this game!\n\n");
                compScore++;
            }
        }
        
        printf("The score is now you: %d  computer: %d", userScore, compScore);
        printf("\n\n\n");
        
        if(userScore == 3 || compScore == 3)
        {
            break;
        }
    }
    
    
    
    return 0;
}

