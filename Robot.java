
package org.usfirst.frc.team5811.robot;



import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
//import org.usfirst.frc.team5811.robot.commands.ExampleCommand;
import org.usfirst.frc.team5811.robot.subsystems.ExampleSubsystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.buttons.JoystickButton; 
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	public static final ExampleSubsystem exampleSubsystem = new ExampleSubsystem();
	//public static OI oi;

    Command autonomousCommand;
    SendableChooser chooser;

    Joystick joyStickLeft;
    Joystick joyStickRight;
    
    JoystickButton button;
    
    Victor frontLeftDriveMotor;
    Victor frontRightDriveMotor;
    Victor backLeftDriveMotor;
    Victor backRightDriveMotor;
    
    int cycleCounter;
    
    //Global Speed Values
    double leftSpeed;
    double rightSpeed;
    
    //COMPRESSOR!!!
    Compressor compressor;
    
    //Limit Switch
    DigitalInput limitSwitch;
    
    //Button boolean
    boolean buttonValue;
    
    
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    private void driveMotors(double speedLeftDM, double speedRightDM) {
    	frontLeftDriveMotor.set(speedLeftDM);
    	frontRightDriveMotor.set(speedRightDM);
    	backLeftDriveMotor.set(speedLeftDM);
    	backRightDriveMotor.set(speedRightDM);
    }
    
    public void robotInit() {
		//oi = new OI();
        chooser = new SendableChooser();
        //chooser.addDefault("Default Auto", new ExampleCommand());
//        chooser.addObject("My Auto", new MyAutoCommand());
        SmartDashboard.putData("Auto mode", chooser);
       
        //Motor port init
       frontLeftDriveMotor = new Victor(0);
       frontRightDriveMotor = new Victor(1);
       backLeftDriveMotor = new Victor(2);
       backRightDriveMotor = new Victor(3);
       
       joyStickLeft = new Joystick(0);
       joyStickRight = new Joystick(1);
       
       button = new JoystickButton(joyStickLeft, 1);
       
       
       //set cycle counter
       cycleCounter = 0;
       
       //compressor port init
       compressor = new Compressor(0);
       compressor.setClosedLoopControl(false);
       
       //limit switch init
       limitSwitch =  new DigitalInput(1);
    }
    
    public void operatorControl(){
    	//call if limit switch is used
    	while(limitSwitch.get()) {
    		System.out.println("limit switch");
    	}
    }
    
    public void disabledInit(){

    }
	
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	
    public void autonomousInit() {
        autonomousCommand = (Command) chooser.getSelected();
        
        driveMotors(0,0);
		/* String autoSelected = SmartDashboard.getString("Auto Selector", "Default");
		switch(autoSelected) {
		case "My Auto":
			autonomousCommand = new Command();
			break;
		case "Default Auto":
		default:
			autonomousCommand = new ExampleCommand();
			break;
		} 
    	
    	// schedule the autonomous command (example)
        if (autonomousCommand != null) autonomousCommand.start();
    }
    	*/
    
    }
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
       
        cycleCounter++;

        if (cycleCounter < 100) {
        	driveMotors(1,0);
        }
        else if (cycleCounter < 200) {
        	driveMotors(0,0);
        }
        else if (cycleCounter < 300) {
        	driveMotors(1,0);
        }
        else if (cycleCounter < 400) {
        	driveMotors(0,0);
        }
        else if (cycleCounter < 500) {
        	driveMotors(1,0);
        }
    }

    public void teleopInit() {
		// This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to 
        // continue until interrupted by another command, remove
        // this line or comment it out.
        if (autonomousCommand != null) autonomousCommand.cancel();
        compressor.setClosedLoopControl(true);
    }
    	
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
        
        //for driving/tank drive
        driveMotors(joyStickLeft.getY(),joyStickRight.getY());
        
        //checking for button values
        //change when button is ready for use
        System.out.println(button.get());
        
        operatorControl();
    }
    
    
    public void testPeriodic() {
        LiveWindow.run();
        
        System.out.println("Trolololol");
        
    }
}
