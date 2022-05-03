# Carol Scout

<p>Carol really liked the "Penguin Carol" simulator that we implemented in homework PINGU CAROL and has already proven to be useful on a few expeditions.</p>
<p>Now Carol came up with more ideas and has a new assignment for us: She would like a program that not only allows the simulation of different situations, but that can even independently find (optimal) paths through the Penguin Carol world to get from to come from one place to the next.</p>
<p><strong>The rules for how Carol can move around the field and what instructions are allowed in certain situations are the same as those described in PINGU CAROL task.</strong></p>
<p>However, the two-dimensional array is passed as a parameter here. In this task you can also assume that all parameters passed are valid. In MiniJava there are still methods for displaying the playing field. You are welcome to use this as you like, because in this task we no longer test for the output on the console.</p>
<h6 id="shortsummary">Short Summary</h6>
<p>As in PINGU CAROL, <code>playground.length</code> is the width of the playing field and <code>playground[0].length</code> is the height. The coordinate system still has its origin at the bottom left and <code>playground[x][y]</code> is the height of the field at position (x, y). The height is a value from <code>-1</code> to <code>9</code>. <code>-1</code> is water that is a block of ice deep. The height is the number of ice blocks above the water, and the number of ice blocks on a field is equal to the height plus one. Carol can move within this playing field and carry around <code>0</code> to <code>10</code> blocks of ice. Carol's viewing direction can be <code>0</code> (pos. X / right), <code>1</code> (pos. Y / up), <code>2</code> (neg. X / left) and <code>3</code> (neg. Y / down). The following instructions can be used to navigate the field:</p>
<ul>
<li><code>'r'</code> Carol turns right from her own point of view.</li>
<li><code>'l'</code> Carol turns left from her own point of view.</li>
<li><code>'s'</code> Carol takes a step in the current direction of gaze. For this, the absolute height difference must be less than or equal to one.</li>
<li><code>'p'</code>  Carol places a block of ice on the field in the line of sight. To do this, she must carry at least one block of ice and the field in front of her must not have reached the maximum height (<code>9</code>). If Carol is in the water, she cannot place blocks of ice either.</li>
<li><code>'n'</code> Carol takes a block of ice from the field in the direction of view. To do this, she must be able to pick up at least one block of ice and the space in front of her must not be water (<code>-1</code>). If Carol is in the water, she cannot take any blocks of ice either.</li>
</ul>
<p>Of course, Carol cannot move outside the field, take or place blocks there.</p>
<h4 id="analyzecarolsinstructions">Analyze Carol's instructions</h4>
<p>First, let's write a few helper methods to better understand Carol's current position on the field. We want to analyze a sequence of Penguin Carol instructions that is stored in an array. It is particularly interesting whether the last instructions stored in an array make sense. So that we can later use the methods efficiently for the pathfinder program, we enter in the parameter filled with two methods how many instructions the array is currently filled with (the point here is to be able to use an array over and over again). None of the following three methods may change the content of the array.</p>
<h5 id="checkifthelasttwistsareuseless">Check if the last twists are useless</h5>
<p>Method head:</p>
<pre class="Java language-Java"><code class="hljs Java language-Java"><span class="hljs-keyword">static</span> <span class="hljs-type">boolean</span> <span class="hljs-title function_">lastTurnsAreUseless</span><span class="hljs-params">(<span class="hljs-type">char</span>[] instr, <span class="hljs-type">int</span> filled)</span>
</code></pre>
<p>The method <em>should return</em> <code>true</code> if and only if the last instructions in the instruction array are rotations that can be achieved differently or even more easily. <br>
Specifically, this is the case if the last two instructions are as follows:</p>
<ul>
<li><code>'r'</code> followed by  <code>'l'</code>, or</li>
<li><code>'l'</code> followed by  <code>'r'</code>, or</li>
<li><code>'r'</code> followed by  <code>'r'</code>, because you can turn around with two  <code>'l'</code> and we don't have to try both.
Or if the last three instructions are as follows:</li>
<li>three times  <code>'l'</code>, because here an  <code>'r'</code> achieves the same thing and is easier.</li>
</ul>
<h5 id="checkthatcarolhasalreadybeentothesameplaceandhasnotchangedanyblocksoficesincethen">Check that Carol has already been to the same place and has not changed any blocks of ice since then</h5>
<p>Method head:</p>
<pre class="Java language-Java"><code class="hljs Java language-Java"><span class="hljs-keyword">static</span> <span class="hljs-type">boolean</span> <span class="hljs-title function_">wasThereBefore</span><span class="hljs-params">(<span class="hljs-type">char</span>[] instr, <span class="hljs-type">int</span> filled)</span>
</code></pre>
<p>The method is to check the instructions starting from the last instruction to see whether Carol has already been in the same place before the last instruction <em>without having placed or taken a block of ice in between</em>. In this case the method should return <code>true</code>.</p>
<h5 id="checktheminimumnumberofstepsandturnsrequired">Check the minimum number of steps and turns required</h5>
<p>Method head:</p>
<pre class="Java language-Java"><code class="hljs Java language-Java"><span class="hljs-keyword">static</span> <span class="hljs-type">int</span> <span class="hljs-title function_">getMinimalStepsAndTurns</span><span class="hljs-params">(<span class="hljs-type">int</span> x, <span class="hljs-type">int</span> y, <span class="hljs-type">int</span> direction, <span class="hljs-type">int</span> findX, <span class="hljs-type">int</span> findY)</span>
</code></pre>
<p>This method should return the minimum number of steps and rotations that are required to get from the current situation (<code>x</code>, <code>y</code>, <code>direction</code>) to the destination (<code>findX</code>, <code>findY</code>). This value should not take into account the heights, blocks of ice or the like (therefore it is only a minimum in relation to the overall situation). <em>However, this minimum must be optimal for the given parameters.</em> (I.e. you may not always return <code>0</code>as a minimum, for example.)</p>
<h4 id="findinginstructions">Finding instructions</h4>
<p>Now we come to the actual part: we want to find paths for Carol to get from an initial situation <code>x</code>, <code>y</code>, <code>direction</code> to a target position <code>findX</code>, <code>findY</code> on a given playing field <code>playground</code>. To do this, implement a method with the following method head:</p>
<pre class="Java language-Java"><code class="hljs Java language-Java"><span class="hljs-keyword">public</span> <span class="hljs-keyword">static</span> <span class="hljs-type">boolean</span> <span class="hljs-title function_">findInstructions</span><span class="hljs-params">(<span class="hljs-type">int</span>[][] playground, <span class="hljs-type">int</span> x, <span class="hljs-type">int</span> y, <span class="hljs-type">int</span> direction, <span class="hljs-type">int</span> blocks, <span class="hljs-type">int</span> findX, <span class="hljs-type">int</span> findY, <span class="hljs-type">char</span>[] instructions)</span>
</code></pre>
<p>In addition to the parameters already mentioned, we also transfer a <code>char</code>-Array <code>instructions</code> to the method. The instruction sequence that leads Carol to her goal is to be stored in this array. Exactly this transferred array should be used, which (as usual for arrays) has a <strong>fixed length.</strong> The method must therefore look for solutions that require a maximum of <code>instructions.length</code> instructions (this also limits the computational effort).</p>
<p>Since it is possible that such a solution does not exist, <code>false</code> should be returned if and only if the search was unsuccessful. However, if the search was successful, <code>true</code> is returned and <code>instructions</code> contain a sequence of instructions with which Carol gets to the goal. If this sequence is shorter than the array, all remaining fields should be filled with <code>'e'</code>, the end instruction in the Penguin Carol simulation. <strong>Solve this problem using recursion.</strong></p>
<p><em>If at least one solution exists that fulfills the requirements passed in the arguments, your implementation of the <code>findInstructions</code> method must find such a solution.</em></p>
<p>Useful notes on implementation:</p>
<ul>
<li>Write a <em>recursive</em> auxiliary method that accepts the same parameters as <code>findInstructions</code> and also an <code>int filled</code> with which you can pass on how many <code>instructions</code> are currently in instructions.</li>
<li>For each recursive call, try out which next instruction leads to the goal. Here you can try out all possible and permitted instructions. Make sure that changes to the tried-out instruction are passed on to the recursive call and, if necessary, reversed later.</li>
<li>Make sure you use the previously implemented methods to analyze the next instructions at the appropriate places in order to be able to exclude options and to determine whether a solution is still possible with the currently selected path. Otherwise your algorithm will be very slow. You should also incorporate additional checks in order to avoid <code>'p'</code> directly after <code>'n'</code> and vice versa.</li>
<li>Some instructions are more "productive" than others, so the order in which you try instructions can also play a role in the speed of execution. Here you can try out what works well, among other things.</li>
<li><em>Optional: You are welcome to implement further optimizations if you want. But then pay close attention to the runtime of these optimizations (and the correctness). Many are not worth the computational effort.</em></li>
</ul>
<h4 id="findanoptimalsequenceofinstructions">Find an optimal sequence of instructions</h4>
<p>Implement the <code>findOptimalSolution</code> method, which has the following method head:</p>
<pre class="Java language-Java"><code class="hljs Java language-Java"><span class="hljs-keyword">public</span> <span class="hljs-keyword">static</span> <span class="hljs-type">char</span>[] findOptimalSolution(<span class="hljs-type">int</span>[][] playground, <span class="hljs-type">int</span> x, <span class="hljs-type">int</span> y, <span class="hljs-type">int</span> direction, <span class="hljs-type">int</span> blocks, <span class="hljs-type">int</span> findX, <span class="hljs-type">int</span> findY, <span class="hljs-type">int</span> searchLimit)
</code></pre>
<p>This method should use <code>findInstructions</code> to find the optimal instruction sequence. An instruction sequence is optimal if there is no shorter one that describes a path from the initial situation to the destination.</p>
<p>The <code>searchLimit</code> is the maximum length up to which solutions should be searched for. This mainly serves to limit the search effort. The method should return <code>null</code> if no such solution exists, and otherwise a char-Array that contains the optimal instruction sequence. The array should then only be as large as necessary (i.e. not filled with an <code>'e'</code>).</p>
<h5 id="atestforamazeexample">A test for a maze example</h5>
<p>The target position is (6, 5), the starting position with start position (0,0), viewing direction below:</p>
<pre style="line-height: 1.2em;"><code>┏━━━┯━━━┯━━━┯━━━┯━━━┯━━━┯━━━┯━━━┯━━━┓
┃ 2 │ ~ │ ~ │ ~ │ ~ │ 2 │ ~ │ ~ │ ~ ┃
┠───┼───┼───┼───┼───┼───┼───┼───┼───┨
┃ ~ │ ~ │ 2 │ ~ │ ~ │ 2 │ ~ │ 2 │ ~ ┃
┠───┼───┼───┼───┼───┼───┼───┼───┼───┨
┃ ~ │ ~ │ ~ │ ~ │ ~ │ ~ │ 2 │ ~ │ ~ ┃
┠───┼───┼───┼───┼───┼───┼───┼───┼───┨
┃ 2 │ 2 │ ~ │ 2 │ 2 │ ~ │ ~ │ ~ │ 2 ┃
┠───┼───┼───┼───┼───┼───┼───┼───┼───┨
┃ ~ │ ~ │ ~ │ 2 │ ~ │ 2 │ ~ │ ~ │ ~ ┃
┠───┼───┼───┼───┼───┼───┼───┼───┼───┨
┃ ~ │ 2 │ ~ │ ~ │ ~ │ 2 │ 2 │ ~ │ ~ ┃
┠───┼───┼───┼───┼───┼───┼───┼───┼───┨
┃ ▼ │ ~ │ ~ │ 2 │ ~ │ ~ │ ~ │ ~ │ 2 ┃ Standing on height -1, carrying 0 ice blocks.
┗━━━┷━━━┷━━━┷━━━┷━━━┷━━━┷━━━┷━━━┷━━━┛</code></pre>
<p>The array for this:</p>
<pre style="line-height: 1.2em;"><code>new int[][] { //
        { -1, -1, -1,  2, -1, -1,  2 }, //
        { -1,  2, -1,  2, -1, -1, -1 }, //
        { -1, -1, -1, -1, -1,  2, -1 }, //
        {  2, -1,  2,  2, -1, -1, -1 }, //
        { -1, -1, -1,  2, -1, -1, -1 }, //
        { -1,  2,  2, -1, -1,  2,  2 }, //
        { -1,  2, -1, -1,  2, -1, -1 }, //
        { -1, -1, -1, -1, -1,  2, -1 }, //
        {  2, -1, -1,  2, -1, -1, -1 }, //
}</code></pre>
