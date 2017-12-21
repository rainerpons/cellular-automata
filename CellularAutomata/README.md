# CellularAutomata

This is a chaos theory project for interacting with cellular automata (CA). Includes a graphical user interface for displaying CA, saving as JPEG, and processing user input. 

## What are cellular automata?

*Cellular automata* are discrete dynamical systems which consist of a *local update rule* and finite state vectors. CA have the following attributes

* *dimension* $d \in \mathbb{N}$
* *finite state set* $S$
* *neighborhood size* $m \in \mathbb{N}$
* *local update rule* $f \! : \! S^m \to S$

*Neighborhood vectors* specify arrays of finite states which are used to update the next cell state.

### Elementary cellular automata

CellularAutomata deals with what are known as *elementary CA*, where dimension is $d = 1$, the finite state set is $\{0, \: 1\}$, and there are $2^8 = 256$ rules.

An *initial seed* is the first neighborhood vector of a CA. For the sake of brevity we will display a neighborhood vector as a binary string

​	$X = (x_0, \: x_1, \: \ldots, \: x_{m -1}) = x_0 x_1 x_2 \ldots x_{m - 1}$.

That is, each $x_i$ either has a value of $0$ or $1$. If we start with an initial seed of $X$, then the next neighborhood vector is given by the local update rule defined by some function

​	$f(X) = r(x_0 x_1 x_2) \: r(x_1 x_2 x_3) \: \cdots \: r(x_{m - 3} x_{m - 2} x_{m - 1})$.

There are $2^8   = 256$ possible rules for the function $r$ and it is up to the user to decide which $r$ to use.

###Example of cellular automata

Consider the elementary CA where the rule number is $30$, the size is $9$, and the initial seed is $000010000$. We take the initial seed and run it through $r$ defined as the following function 

​	$r(x) = \begin{cases} 1 & \text{if } x = 100, \: 011, \: 010, \: 011 \\ 0 & \text{if } x = 111, \: 110, \: 101, \: 000 \end{cases}$

This gives us the next neighborhood vector, which we then put back into the function $r$, and we repeat the process as desired. Mathematically this looks like

​	$\begin{align*} f(000010000) &= r(000) \: \cdots \: r(001) \: r(010) \: r(100) \: \cdots \: r(000) = 000111000 \\ f(000111000) &= r(000) \: \cdots \: r(011) \: r(111) \: r(110) \: \cdots \: r(000) = 001100100 \\ &\;\; \vdots \end{align*}$

You can use CellularAutomata to generate an arbitrary number of neighborhood vectors

~~~java
int size = 9;
int rule = 30;
Vector seed = Generator.generateSparseSeed(size);
System.out.println("initial seed: \t" + seed.getVector());
Vector gen1 = Generator.generateSuccessor(rule, seed);
System.out.println("generation 1: \t" + gen1.getVector());
Vector gen2 = Generator.generateSuccessor(rule, gen1);
System.out.println("generation 2: \t" + gen2.getVector());
// ...
Vector gen8 = Generator.generateSuccessor(rule, gen7);
System.out.println("generation 8: \t" + gen8.getVector());
~~~

Given the above code the output should look like

~~~
initial seed: 	000010000
generation 1: 	000111000
generation 2: 	001100100
generation 3: 	011011110
generation 4: 	110010001
generation 5: 	101111011
generation 6: 	101000010
generation 7: 	101100111
generation 8: 	101011100
~~~

