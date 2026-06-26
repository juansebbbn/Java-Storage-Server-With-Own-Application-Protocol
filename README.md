# J-Cloud - Secure Binary Object Storage

J-Cloud is a binary object storage system designed to simulate the behavior of cloud storage services such as Amazon S3 while prioritizing security and efficiency through a custom application-layer communication protocol.

Unlike traditional REST-based storage services, J-Cloud communicates using a proprietary binary protocol instead of HTTP, significantly reducing the attack surface and allowing only compliant clients to interact with the server.

---

# Project Philosophy

The primary goal of J-Cloud is to demonstrate how a custom communication protocol can improve security by eliminating dependencies on traditional web protocols.

By avoiding HTTP entirely, the system is naturally protected against many common web attacks, including:

* HTTP Header Injection
* HTTP-specific Denial of Service attacks
* Malformed HTTP requests
* Web framework exploits

Only clients capable of generating the exact binary frame expected by the server can establish successful communication, providing **security by design**.

---

# Binary Communication Protocol

Every request sent to the server must strictly follow the binary frame specification below.

Any malformed packet is immediately rejected and the connection is terminated.

## Frame Structure

| Field        | Size     | Description                                               |
| ------------ | -------- | --------------------------------------------------------- |
| Operation ID | 1 byte   | Requested operation (e.g. `0x01` Upload, `0x02` Download) |
| User ID      | 4 bytes  | Unique identifier of the client                           |
| File Size    | 8 bytes  | Total size of the file in bytes                           |
| Name Size    | 4 bytes  | Length of the filename                                    |
| File Name    | Variable | Object name                                               |
| Payload      | Variable | Binary file content                                       |

---

# System Architecture

The project is divided into two independent modules.

## Core Module (Server)

The Core Module is responsible for accepting client connections, validating requests, encrypting data, and persisting objects.

### Components

### Connection Receiver

Responsible for:

* Listening for incoming TCP connections
* Receiving binary frames
* Validating packet integrity
* Rejecting malformed requests

---

### Encryption Service

Responsible for:

* Encrypting every uploaded payload
* Protecting data at rest
* Ensuring stored objects cannot be read without the corresponding encryption keys

---

### Storage Service

Responsible for:

* Persisting encrypted objects
* Managing the server file repository
* Retrieving stored files

---

# Dashboard Module

The Dashboard is an independent visualization component.

Instead of communicating directly with clients, it monitors the storage directory and provides a graphical overview of the stored objects.

Its main purpose is repository auditing and storage monitoring.

---

# Project Architecture

```text id="h7lqvh"
J-Cloud/

├── core/
│   ├── networking/
│   │   └── TCP Server
│   ├── protocol/
│   │   └── Binary Frame Parser
│   ├── encryption/
│   │   └── Encryption Service
│   ├── storage/
│   │   └── Storage Service
│   └── repository/
│
├── dashboard/
│   └── Storage Viewer
│
└── README.md
```

---

# Features

* Custom binary application-layer protocol
* HTTP-free architecture
* Secure binary object storage
* Payload encryption before persistence
* TCP-based communication
* Strict packet validation
* Modular architecture
* Independent storage dashboard
* Server-side object management

---

# How It Works

1. A client establishes a TCP connection.
2. The client sends a binary frame following the protocol specification.
3. The server validates the frame.
4. The payload is encrypted.
5. The encrypted object is stored on disk.
6. The Dashboard automatically detects and displays the new object.

---

# Technologies

* Java
* TCP Sockets
* Java NIO / IO
* Custom Binary Protocol
* Cryptography APIs
* Multi-threading

---

# Future Improvements

* User authentication
* Object metadata indexing
* Folder support
* Distributed storage nodes
* Compression before encryption
* File versioning
* Access Control Lists (ACL)
* Web administration panel
* Automatic key rotation

---

# Author

**Juan**

## Version

**1.0.0**
