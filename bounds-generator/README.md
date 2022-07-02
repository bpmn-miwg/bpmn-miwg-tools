# Bounds Generator

Given a folder filled with bpmn files (*.bpmn), this class generates transparent PNGs in a destination folder with the bounds present in the bpmn diagrams.

For each file found in the source folder a serie a 3 PNGs are generated (ex: file.bpmn):

	file_X_all_bounds.png: All bounds
	file_X_label_bounds.png: Only label bounds
	file_X_shapes_bounds.png: Only shape bounds

Where X is a diagram index (1 based). 

For backward compatibility, it also generates a version of the 3 PNGs without the _X (ex: file_all_bounds.png).
 
## Usage
org.omg.bpmn.miwg.boundary.BoundaryCreator is the main class and need to be invoked with 2 parameters

java -jar bounds-generator.jar bpmn_source_folder png_target_folder