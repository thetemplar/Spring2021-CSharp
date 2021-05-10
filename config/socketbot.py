import sys
import math
import random
import socket

arguments = len(sys.argv) - 1

HOST = '127.0.0.1'  # Standard loopback interface address (localhost)

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.bind((HOST, int(sys.argv[1])))
    s.listen()
    conn, addr = s.accept()
    with conn:
        number_of_cells = int(input())  # 37
        for i in range(number_of_cells):
            # index: 0 is the center cell, the next cells spiral outwards
            # richness: 0 if the cell is unusable, 1-3 for usable cells
            # neigh_0: the index of the neighbouring cell for each direction
            index, richness, neigh_0, neigh_1, neigh_2, neigh_3, neigh_4, neigh_5 = [int(j) for j in input().split()]

        while True:
            _in = input()
            while _in == "WAIT" or _in.startswith("SEED") or _in.startswith("GROW") or _in.startswith("COMPLETE"):
                _in = input()
            _round = int(_in)
            nutrients = int(input())
            sun, score = [int(i) for i in input().split()]
            other_sun, other_score, other_is_waiting = [int(i) for i in input().split()]
            number_of_trees = int(input())
            size = [None] * 37
            tree_owner = [None] * 37
            is_dormant = [None] * 37
            for i in range(number_of_trees):
                _cell_index, _size, _tree_owner, _is_dormant = [int(j) for j in input().split()]
                size[_cell_index] = _size
                tree_owner[_cell_index] = _tree_owner
                is_dormant[_cell_index] = _is_dormant

            possible_move_number = int(input())

            conn.send((str(_round) + "\r\n").encode())
            conn.send((str(nutrients) + "\r\n").encode())
            conn.send((str(sun) + "\r\n").encode())
            conn.send((str(score) + "\r\n").encode())
            conn.send((str(other_sun) + "\r\n").encode())
            conn.send((str(other_score) + "\r\n").encode())
            conn.send((str(other_is_waiting) + "\r\n").encode())
            conn.send((str(possible_move_number) + "\r\n").encode())
            for i in range(37):
                conn.send((str(size[i]) + "\r\n").encode())
                conn.send((str(tree_owner[i]) + "\r\n").encode())
                conn.send((str(is_dormant[i]) + "\r\n").encode())


            #print("WAIT")
            print(conn.recv(1024).decode("utf-8"))