package main;

import fr.lri.swingstates.canvas.CExtensionalTag;
import fr.lri.swingstates.canvas.CRectangle;
import fr.lri.swingstates.canvas.CStateMachine;
import fr.lri.swingstates.canvas.Canvas ;
import fr.lri.swingstates.canvas.CShape ;
import fr.lri.swingstates.canvas.CText ;
import fr.lri.swingstates.canvas.transitions.EnterOnTag;
import fr.lri.swingstates.canvas.transitions.LeaveOnTag;
import fr.lri.swingstates.canvas.transitions.PressOnTag;
import fr.lri.swingstates.canvas.transitions.ReleaseOnTag;
import fr.lri.swingstates.debug.StateMachineVisualization;
import fr.lri.swingstates.sm.State;
import fr.lri.swingstates.sm.Transition;
import fr.lri.swingstates.sm.transitions.Release;
import fr.lri.swingstates.sm.transitions.TimeOut;

import javax.swing.JFrame ;
import javax.swing.WindowConstants;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font ;

/**
 * @author Nicolas Roussel (roussel@lri.fr)
 *
 */
public class SimpleButton {

    private CText label;
    private CRectangle rectangle;
    private CExtensionalTag tag;

    SimpleButton(Canvas canvas, String text, int button) {
    	rectangle = canvas.newRectangle(0, 0, 150, 50);
    	rectangle.setFillPaint(Color.WHITE);
    	tag = new CExtensionalTag(canvas) {};
    	
    	CStateMachine stateMachine = initStateMachine(button);
    	StateMachineVisualization smv = initStateMachineVisualization(stateMachine);
    	
    	JFrame smvFrame = new JFrame();
    	smvFrame.add(smv);
    	smvFrame.pack();
    	smvFrame.setVisible(true);
    	smvFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    	
    	label = canvas.newText(0, 0, text, new Font("verdana", Font.PLAIN, 12));
    	label.addChild(rectangle);
    	label.above(rectangle);
    	
    	rectangle.addTag(tag);
    	label.addTag(tag);
    	
    	stateMachine.attachTo(rectangle);
    }

    public void action() {
	   System.out.println("ACTION!");
    }

    public CShape getShape() {
	   return label;
    }
    
    public CStateMachine initStateMachine(int button) {
    	CStateMachine sm = new CStateMachine() {
    		
    		// état start
    		public State start = new State() {
    			
    			public void enter() {
    				label.setText("start");
    				rectangle.setStroke(new BasicStroke(1));
    				rectangle.setFillPaint(Color.WHITE);
    				
    			}
    			
    			Transition onHover = new EnterOnTag(tag, ">> hover");
    		};
    		
    		// état hover
    		public State hover = new State() {
    			
    			public void enter() {
    				label.setText("hover");
    				rectangle.setStroke(new BasicStroke(2));
    				rectangle.setFillPaint(Color.WHITE);
    			}
    			
    			Transition notOnHover = new LeaveOnTag(tag, ">> start");
    			
    			Transition press = new PressOnTag(tag, button, ">> pressed");
    			
    		};
    		
    		// état hold
    		public State hold = new State() {
    			
    			public void enter() {
    				label.setText("hold");
					rectangle.setStroke(new BasicStroke(1));
					rectangle.setFillPaint(Color.WHITE);
    			}
    			
    			Transition rentre = new EnterOnTag(tag, ">> pressed");
    			
    			Transition releaseOutside = new ReleaseOnTag(tag, button, ">> start");
    		};
    		
    		// état half-hold
    		public State halfhold = new State() {
    			
    			public void enter() {
    				label.setText("half hold");
    				rectangle.setStroke(new BasicStroke(1));
					rectangle.setFillPaint(Color.WHITE);
    			}
    			
    			Transition rentre = new EnterOnTag(tag, ">> halfclick");
    			
    			Transition releaseOutside = new Release(button, ">> start");
    		};
    		
    		// état pressed
    		public State pressed = new State() {
    			
    			public void enter() {
    				label.setText("pressed");
    				rectangle.setStroke(new BasicStroke(1));
					rectangle.setFillPaint(Color.YELLOW);
    				armTimer(500, false);
    			}
    			
    			Transition timeout = new TimeOut(">> halfclick");
    			
    			Transition release = new ReleaseOnTag(tag, button, ">> waitdouble");
    			
    			Transition sortDuBouton = new LeaveOnTag(tag, ">> hold");
    			
    		};
    		
    		// état half-click
    		public State halfclick = new State() {
    			
    			public void enter() {
    				label.setText("half click");
    				rectangle.setStroke(new BasicStroke(1));
    				rectangle.setFillPaint(Color.ORANGE);
    			}
    			
    			Transition release = new ReleaseOnTag(tag, button, ">> hover");
    			
    			Transition sortDuBouton = new LeaveOnTag(tag, ">> halfhold");
    		};
    		
    		// état waitdouble
    		public State waitdouble = new State() {
    			
    			public void enter() {
    				label.setText("waiting double");
    				rectangle.setStroke(new BasicStroke(2));
					rectangle.setFillPaint(Color.YELLOW);
    				armTimer(500, false);
    			}
    			
    			Transition timeout = new TimeOut(">> hover");

    			Transition doublePressed = new PressOnTag(tag, button, ">> doublepressed");
    			
    			Transition sortDuBouton = new LeaveOnTag(tag, ">> start");
    			
    		};
    		
    		// état double pressed
    		public State doublepressed = new State() {
    			
    			public void enter() {
    				label.setText("double pressed");
    				rectangle.setStroke(new BasicStroke(1));
					rectangle.setFillPaint(Color.YELLOW);
					armTimer(300, false);
    			}
    			
    			Transition doubleclick = new ReleaseOnTag(tag, button, ">> doubleclick");
    			
    			Transition timeout = new TimeOut(">> oneclickandhalf");
    			
    		};
    		
    		// état one click and half
    		public State oneclickandhalf = new State() {
    			public void enter() {
    				label.setText("one click and half");
    				rectangle.setStroke(new BasicStroke(1));
					rectangle.setFillPaint(Color.ORANGE);
    			}
    			
    			Transition release = new ReleaseOnTag(tag, button, ">> hover");
    			
    			Transition sortDuBouton = new LeaveOnTag(tag, ">> oneclickandhalfhold");
    		};
    		
    		// état one click and half hold
    		public State oneclickandhalfhold = new State() {
    			public void enter() {
    				label.setText("one click and half hold");
    				rectangle.setStroke(new BasicStroke(1));
					rectangle.setFillPaint(Color.WHITE);
    			}
    			
    			Transition rentre = new EnterOnTag(tag, ">> oneclickandhalf");
    			
    			Transition releaseOutside = new Release(button, ">> start");
    			
    		};
    		
    		// état double click
    		public State doubleclick = new State() {
    			public void enter() {
    				label.setText("double click");
    				rectangle.setStroke(new BasicStroke(2));
					rectangle.setFillPaint(Color.RED);
					armTimer(1000, false);
    			}
    			
    			Transition timeout = new TimeOut(">> hover");
    			
    			Transition newclick = new PressOnTag(tag, button, ">> pressed");
    			
    			Transition sortDuBouton = new LeaveOnTag(tag, ">> start");
    			
    		};
    		
		};
		return sm;
    }
    
    public StateMachineVisualization initStateMachineVisualization(CStateMachine sm) {
    	StateMachineVisualization smv = new StateMachineVisualization(sm);
    	
    	return smv;
    }

    static public void main(String[] args) {
	   JFrame frame = new JFrame();
	   Canvas canvas = new Canvas(400,400);
	   frame.getContentPane().add(canvas);
	   frame.pack();
	   frame.setVisible(true);
	   frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

	   int button = 1;
	   try {
		   int i = Integer.parseInt(args[0]);
		   if(i > 0 && i < 4)
			   button = i;
	   } catch(Exception e) {
		   
	   }

	   SimpleButton simple = new SimpleButton(canvas, "simple", button);
	   simple.getShape().translateBy(100,100);
    }

}