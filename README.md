# LCXShared v0.1
A library to support client communication with an instance of the LCX-Server project. 

Public functions of the main class, LCXDelegate, include:
 - boolean login(accountID, password)
 - void logout()
 - boolean transfer(accountID, amount)
 - String balance()
 - void dispose()

This version, release-0.1, implements communication protocol version 0.1
