# Proofer
"Proofer" is a program that allows users to design, customize, and generate geometric proofs. Behind the scenes a proof-solving engine parses the user's input and generates an in depth solution.

<b>Tools:</b>
* JavaFX
***
The user first designs the proof's diagram by adding triangles, segments, circles, and polygons. The user can arrange them to their liking.
<br><br>
<img src="https://github.com/DavidDinkevich/Proofer/blob/4d983bacfde4a50f03062cdbd8e05690292876f7/Gallery/diagram_example.png" width="700"/>
<br>
Next, in the "Given" section, the user can add a variety of relationships between the figures, such as:
  * "Angle ABC is a right angle"
  * "Segments AB and CD are congruent"

Lastly, the user enters the proof goal, i.e., the relationship that they want to the computer to prove. And... that's it. Once the "Prove" button is clicked, the engine handles the rest.
<br>
For the above example, the engine outputs:
<br><br>
<img src="https://github.com/DavidDinkevich/Proofer/blob/4d983bacfde4a50f03062cdbd8e05690292876f7/Gallery/solution_example.png" width="700"/>

The different logical sections of the proof are broken up and color coded for convenience.
