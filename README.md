# Java Multi-Threaded Socket Chat

A robust client-server chat application built with Java Sockets and Swing. This project demonstrates concurrent programming, network communication, and thread-safe graphical user interface updates.

## 🚀 Key Features

*   **Multithreaded Architecture:** The server creates a dedicated thread (`ManejadorCliente`) for each connected user, allowing asynchronous, non-blocking communication for multiple clients simultaneously.
*   **Persistent Connections:** The application maintains a persistent, stateful TCP Socket connection for the entire session to ensure fast, real-time message delivery without the overhead of opening and closing sockets for every message.
*   **Dynamic Routing (HashMap):** Uses a `HashMap` on the server side to map usernames directly to their respective `ObjectOutputStream`, enabling accurate and efficient direct messaging instead of broadcasting to all IPs.
*   **Thread-Safe UI:** Implements `SwingUtilities.invokeLater()` to safely update Java Swing interface components (like `JTextArea` and `JComboBox`) from background network threads, preventing UI freezes and concurrency errors.
*   **Object Serialization:** Data routing is handled through a custom serializable `Atributos` class, which encapsulates the message type (connection/message), sender, recipient, and payload.

## 🧠 Network & Concurrency Implementation

*   **Server:** Listens on port `12345`. Upon accepting a connection via `server.accept()`, it spawns a new thread to handle the client's I/O streams without blocking other incoming connections.
*   **Client:** Opens a single connection to the server. It runs a continuous background thread to listen for incoming serialized objects, differentiating between system notifications (like a new user connecting) and standard chat messages.

## 🛠️ Requirements & Usage

To compile and run this application, you need the Java Development Kit (JDK) installed.

### Execution

1. Compile the Java files:
   ```bash
   javac Servidor.java Cliente.java Atributos.java
2. Start the Server first:
   ```bash
   java Servidor
3. Start one or more Clients (you can run this multiple times on the same machine to simulate a network):
   ```bash
   java Cliente
4. Enter a username when prompted, select an active user from the dropdown, and start chatting!
   
