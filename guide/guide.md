# **Usage Guide**

## **Features Implemented**

### **1. Add Players**

- **Endpoint:** **`/api/player/add-player`**
- **Method:** POST
- **Authorization:** Required
- **Description:** Adds a new player to the game.
- **Request Body:**
    
    ```json
    
    {
      "playerName": "Player Name",
       "playerEmail": "player@example.com",
       "isHost": false
    }
    
    ```
    
- **Response:** Returns the newly added player object.

### **2. Add Place with Authorization**

- **Endpoint:** **`/api/place/add-place/{playerId}`**
- **Method:** POST
- **Authorization:** Required (Host Player)
- **Description:** Adds a new place to the game board.
- **Request Body:**
    
    ```json
    
    {
      "placeName": "Place Name",
      "buyPrice": 200,
      "rentPrice": 50
    }
    
    ```
    
- **Response:** Returns the newly added place object.

### **3. Create Game with Authorization**

- **Endpoint:** **`/api/game/create-game/{hostPlayerId}/{player2Id}`**
- **Method:** POST
- **Authorization:** Required{Host}
- **Description:** Creates a new game with two players passed as path variables.
- **Response:** Returns the newly created game object.

### **4. Roll Dice and Inform Place Landed**

- **Endpoint:** **`/roll-dice/{gameId}/{playerId}`**
- **Method:** POST
- **Authorization:** Required
- **Description:** Rolls two dice for the player and informs about the place landed.
- **Response:** Informs about the dice roll result and the place landed.

### **5. Auto Purchase Place when Landed**

- **Description:** Automatically purchases a place if unowned and player has sufficient balance.

### **6. Auto Pay Rent to Owner**

- **Description:** Automatically pays rent to the owner if the place is owned by another player.

### **7. Gain +$200 on Crossing Start**

- **Description:** Adds $200 to the player's balance when crossing the start position.

### **8. Declare Winner**

- **Description:** Declares the winner of the game based on bankruptcy or highest cash before turn 50.
- **Reset:** Resets all ownership of places and sets players' cash balances to $1000 when the game is over.

## **Usage Instructions**

1. **Add Players:**
    - Use the **`/add-player`** endpoint with a POST request to add players to the game.
2. **Add Place with Authorization:**
    - Use the **`/add-place`** endpoint with a POST request and provide authorization as the host player to add places to the game board.
3. **Create Game with Authorization:**
    - Use the **`/create-game`** endpoint with a POST request to create a new game with two players.
4. **Roll Dice and Inform Place Landed:**
    - Use the **`/roll-dice/{gameId}/{playerId}`** endpoint with a POST request to roll dice for a player and get informed about the place landed and perform the below Automatic actions, special actions and actions performed after the game is over.
5. **Automatic Actions:**
    - Auto purchase places when landed on an unowned place with sufficient balance.
    - Auto pay rent to the owner when landed on a place owned by another player.
6. **Special Actions:**
    - Gain +$200 when crossing the start position.
    - Declare the winner based on game conditions such as bankruptcy or highest cash before turn 50.
7. **Game Over:**
    - When the game is over and a winner is declared, all ownership of places is cleared, and players' cash balances are reset to $1000.

## **Authorization**

- Authorization is required for certain actions such as adding players, creating games, and adding places. Ensure you have the necessary permissions to perform these actions.

## **Error Handling**

- The API handles errors gracefully and provides meaningful error messages for invalid requests or unauthorized actions.