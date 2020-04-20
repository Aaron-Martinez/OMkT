# OMkT
Optimal Markov k-Tree implementation of the algorithm described in this paper: https://arxiv.org/abs/1801.06900

This code can efficiently compute the maximum/minimum spanning k-tree of an undirected graph with a special constraint that all backbone edges be retained. The program makes use of dynamic programming and parallelism to combat computational complexity. See the paper above for proof a of the O( n^(k+1) ) complexity of this algorithm, where n is the number of graph vertices, and k is the tree width.

Execution time may vary by machine. Below are some example results on my machine using test1.txt setting n = 20
k = 3  572 milliseconds
k = 4  7 seconds, 316 milliseconds
k = 5  3 minutes, 16 seconds
