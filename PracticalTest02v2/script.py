import socket
import datetime
import time


def start_server():
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind(('0.0.0.0', 12345))
    server_socket.listen(1)
    print("Server started on port 12345")

    while True:
        client_socket, addr = server_socket.accept()
        print(f"Connection from {addr}")
        try:
            while True:
                now = datetime.datetime.now().strftime("%H:%M:%S")
                client_socket.sendall(now.encode('utf-8') + b'\n')
                time.sleep(1)  # Pause for 1 second
        except (BrokenPipeError, ConnectionResetError):
            print(f"Connection with {addr} closed")
        finally:
            client_socket.close()


if __name__ == "__main__":
    start_server()
