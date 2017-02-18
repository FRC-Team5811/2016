package org.usfirst.frc.team5811.robot;



import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import org.usfirst.frc.team5811.robot.commands.ExampleCommand;
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
	public static OI oi;

    Command autonomousCommand;
    SendableChooser chooser = new SendableChooser();

    Joystick joyStickLeft;
    Joystick joyStickRight;
    //Joystick xbox;
    Joystick logitech;
    
    JoystickButton buttonA;
    JoystickButton buttonY;
    JoystickButton buttonB;
    JoystickButton bumperRight;
    JoystickButton bumperLeft;
    JoystickButton logitechY;
    JoystickButton logitechA;
    JoystickButton logitechX;
    JoystickButton logitechB;
    JoystickButton logitechLeftBumper;
    JoystickButton logitechRightBumper;
    JoystickButton xboxStartButton;
    JoystickButton xboxBackButton;
    int direction; 
    //int logitechDirection;
    boolean stupidIntake;
    
    Victor frontLeftDriveMotor;
    Victor frontRightDriveMotor;
    Victor backLeftDriveMotor;
    Victor backRightDriveMotor;
    Victor intake;
    
    double intakePower; 
    //AnalogInput intakeUltrasonic;d
    
    double autoSelecter;
    boolean releaseToggle;
    boolean raiseAfterRelease;
    boolean returnAfter;
    double turnTime;
    double turnPower;
    boolean shootBall;
    
    int cycleCounter;
    int intakeCounter;
    boolean firstSpikeStarted;
    boolean firstSpikeFinished;
    boolean secondSpikeStarted;
    int secondIntakeCounter;
    double ultrasonicVoltage;
    int rightThreshold;
    int centerThreshold;
    int leftThreshold;
    
    double leftTrim;
    double rightTrim;
    
    //Global Speed Values
    double leftSpeed;
    double rightSpeed;
    
    double throttleGain;
    double turningGain;
    
    double current;
    
    //A cylinder
    DoubleSolenoid cylinder;
    DoubleSolenoid scalePistons;
    
    //COMPRESSOR!!!
    Compressor compressor;
    
    //Limit Switch
    DigitalInput limitSwitch;
       
    //Button boolean
    boolean state;
    boolean previousState;
    
    //power distribution panel
    PowerDistributionPanel power = new PowerDistributionPanel();
    
    //For the pneumatic
    double dpad;
    double logitechDPad;
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    private void driveMotors(double speedLeftDM, double speedRightDM) {
    	System.out.println("Command: " + speedLeftDM);
    	frontLeftDriveMotor.set(speedLeftDM);
    	frontRightDriveMotor.set(speedRightDM);
    	backLeftDriveMotor.set(speedLeftDM);
    	backRightDriveMotor.set(speedRightDM);
    }
    
    
    private void arcadeDrive(double throttle, double turn){
    	leftSpeed = throttle * throttleGain + turn * turningGain;
    	rightSpeed = throttle * throttleGain - turn * turningGain;
    	
    	driveMotors(leftSpeed, rightSpeed);
    	
    }
    
    private void IntakeOnOff(double val){
    	intake.set(val);
    }
    
    public void robotInit() {
 
		oi = new OI();
        chooser = new SendableChooser();
        chooser.addDefault("Default Auto", new ExampleCommand());
        //chooser.addObject("My Auto", new MyAutoCommand());
        chooser.addObject("My Auto", "My Auto");
        SmartDashboard.putData("Auto mode", chooser);
        System.out.println(SmartDashboard.getBoolean("DB/Button 0", false));
  
        //Motor port init
       frontLeftDriveMotor = new Victor(2);
       frontRightDriveMotor = new Victor(0);
       backLeftDriveMotor = new Victor(3); 
       backRightDriveMotor = new Victor(1);
       
       intake = new Victor(4);
       
       //joyStickLeft = new Joystick(0);
       //joyStickRight = new Joystick(1);
       logitech = new Joystick(0);
       //xbox = new Joystick(1);
       
       
       //buttonA = new JoystickButton(xbox, 1);
       //buttonY = new JoystickButton(xbox, 4);
       //buttonB = new JoystickButton(xbox, 2);
       //bumperRight = new JoystickButton(xbox, 6);
       //bumperLeft = new JoystickButton(xbox, 5);
       logitechY= new JoystickButton(logitech, 3);
       logitechA= new JoystickButton(logitech, 1);
       logitechX= new JoystickButton(logitech, 0);
       logitechB= new JoystickButton(logitech, 2);
       logitechLeftBumper= new JoystickButton(logitech, 4);
       logitechRightBumper= new JoystickButton(logitech, 5);
       //xboxStartButton = new JoystickButton(xbox, 9);
       //xboxBackButton = new JoystickButton(xbox, 10);
       stupidIntake = false;
       
       rightTrim = SmartDashboard.getNumber("DB/Slider 3", 1.0);
       if(rightTrim == 0){ SmartDashboard.putNumber("DB/Slider 3", 1); rightTrim = 1;}
       
       cylinder = new DoubleSolenoid(2,1);//port 0 failed, changed to 2
       scalePistons = new DoubleSolenoid(3,4);//One solenoid two pistons
       cylinder.set(DoubleSolenoid.Value.kReverse);
       //scalePistons.set(DoubleSolenoid.Value.kReverse);
       
       //set cycle counter
       cycleCounter = 0;
          
       //compressor port init
       compressor = new Compressor(0);
       compressor.setClosedLoopControl(false);
       
       throttleGain = 1;
       turningGain = .8;
       
       
       
       //limit switch init
       limitSwitch =  new DigitalInput(1);
       
       current = power.getCurrent(15);
       System.out.println(current);
    
    }
   
    private void operatorControl(){
    	//call if limit switch is used
    	/*while(limitSwitch.get()) {
    		System.out.println("limit switch");
    	}*/
    }
    
    public void disabledInit(){

    }
	
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	
    public void autonomousInit() {
        autonomousCommand = (Command) chooser.getSelected();
        cycleCounter = 0;
        autoSelecter = SmartDashboard.getNumber("DB/Slider 0", 0.0);
        turnPower = SmartDashboard.getNumber("DB/Slider 1", 0.0);
        turnTime = SmartDashboard.getNumber("DB/Slider 2", 0.0);
        releaseToggle = SmartDashboard.getBoolean("DB/Button 0", false);
        raiseAfterRelease = SmartDashboard.getBoolean("DB/Button 1", false);
        returnAfter = SmartDashboard.getBoolean("DB/Button 2",false);
        shootBall = SmartDashboard.getBoolean("DB/Button 3",false);
        System.out.println(autoSelecter);
        System.out.println(releaseToggle);
        System.out.println(raiseAfterRelease);
        System.out.println(returnAfter);
        //if(autoSelecter == 0) autoRoutine=0;
        
        cylinder.set(DoubleSolenoid.Value.kReverse);
        
        rightTrim = SmartDashboard.getNumber("DB/Slider 3", 1);
        if(rightTrim == 0){ SmartDashboard.putNumber("DB/Slider 3", 1); rightTrim = 1;}
        
        //driveMotors(0,0);
		 String autoSelected = SmartDashboard.getString("Auto Selector", "Default");
		switch(autoSelected) {
		case "My Auto":
			autonomousCommand = new ExampleCommand();
			break;
		case "Default Auto":
		default:
			autonomousCommand = new ExampleCommand();
			break;
		} 
    	
    	// schedule the autonomous command (example)
        if (autonomousCommand != null) autonomousCommand.start();
        driveMotors(0,0);
    }
    	
        
    
    
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
       
        cycleCounter++;
        
        if(autoSelecter == 0){       // REACH
        	if(cycleCounter < 250) driveMotors(-0.3, 0.3);
        	else driveMotors(0,0);
        }else if(autoSelecter == 1){ // LOW BAR
        	cylinder.set(DoubleSolenoid.Value.kForward);
        	if(cycleCounter < 100){ driveMotors(-0.0, 0.0);
        	}else if(cycleCounter < 250){ driveMotors(-0.5, 0.5);
        	}else driveMotors(0,0);
        	
        	if(releaseToggle && cycleCounter > 275){ //Ball Release Sequence
        		//if(cycleCounter > 450) cylinder.set(DoubleSolenoid.Value.kReverse);
        		if(cycleCounter > 275 && cycleCounter < 300) IntakeOnOff(1);
        		else{IntakeOnOff(0); }//if(raiseAfterRelease){ cylinder.set(DoubleSolenoid.Value.kForward);}}
        	}
        	//Arm Raising Sequence
        	//if(raiseAfterRelease && cycleCounter>300){ cylinder.set(DoubleSolenoid.Value.kForward);}
        	
        	if(returnAfter){
        		if(cycleCounter > 300 && cycleCounter < 450) driveMotors(.5,-.5);
        		else driveMotors(0,0);
        	}
        	//if(shootBall){
    		//	if(cycleCounter > 515) driveMotors(0,-.4);
    		//}
        		
        	//}
        
        }else if(autoSelecter == 1.5){ // LOW BAR LOW GOAL
        	cylinder.set(DoubleSolenoid.Value.kForward);
        	if(cycleCounter < 100) driveMotors(-0.0, 0.0);
        	else if(cycleCounter < 200) driveMotors(-0.5, 0.5);
        	else if(cycleCounter < 400){ 
        		cylinder.set(DoubleSolenoid.Value.kReverse);
        		driveMotors(-0.3, 0.5);
        	}else if(cycleCounter < 500){ 
        		driveMotors(0,0);
        		cylinder.set(DoubleSolenoid.Value.kForward);
        	}else if(cycleCounter < 600){ driveMotors(-.2,.2);
        	}else if(cycleCounter < 665){ IntakeOnOff(1);
        	}else if(cycleCounter < 735){ IntakeOnOff(-1);
        	}else if(cycleCounter < 750){ 
        		IntakeOnOff(0);
        		driveMotors(0,0);
        	}
        	
        }else if(autoSelecter == 2){ // ROUGH TERRAIN
        	if(cycleCounter < 35) driveMotors(-0.3, 0.3);
        	else if(cycleCounter < 150) driveMotors(-0.75, 0.75);
        	else driveMotors(0,0); 
        	
        	if(releaseToggle && cycleCounter > 165){ //Ball Release Sequence
        		if(cycleCounter > 165) cylinder.set(DoubleSolenoid.Value.kReverse);
        		if(cycleCounter > 205 && cycleCounter < 230) IntakeOnOff(1);
        		else{IntakeOnOff(0);}// if(raiseAfterRelease){ cylinder.set(DoubleSolenoid.Value.kReverse);}}
        	}
        	//Arm Raising Sequence
        	if(raiseAfterRelease && cycleCounter>230){ cylinder.set(DoubleSolenoid.Value.kForward);}
        	
        	if(returnAfter && cycleCounter > 300){ //Return Sequence
        		if( cycleCounter < 415){ driveMotors(.7,-.7);
        		}else if( cycleCounter < 450){ driveMotors(.3,-.3);
        		}else driveMotors(0,0);
        	}
        	
        }else if(autoSelecter == 2.5){ // CHEVAL DE FRISE
        	if(cycleCounter < 200){driveMotors(-0.35,0.35);
        	}else if(cycleCounter < 250){ cylinder.set(DoubleSolenoid.Value.kForward); driveMotors(0,0);
        	}else if(cycleCounter < 262){ driveMotors(0.2,-0.2);
        	}else if(cycleCounter < 310){ driveMotors(-0.6,0.6);
        	}else if(cycleCounter < 350){ driveMotors(-0.3,0.3);
        	}else if(cycleCounter < 375){ driveMotors(0,0);
        	}
        	
        		
        }else if(autoSelecter == 3){ // ROCKWALL
        	
        	/*if(cycleCounter < 40) driveMotors(-0.3, 0.3);    //Forwards rock wall
        	else if(cycleCounter < 150)cylinder.set(DoubleSolenoid.Value.kForward);
        	else if(cycleCounter < 175)driveMotors(-0.8,0.8);
        	else driveMotors(0,0);*/
        	
        	if(cycleCounter <40){ driveMotors(0.25,-0.25);    //Backwards rock wall
        	}else if(cycleCounter <100){cylinder.set(DoubleSolenoid.Value.kForward);  
        	}else if(cycleCounter <175){driveMotors(0.6,-0.6); IntakeOnOff(-.8);
        	}else if(cycleCounter <200){driveMotors(0.7,-0.7); 
        	}else if(cycleCounter <215){driveMotors(0.8,-0.8); 
        	//else if(cycleCounter <250)driveMotors(0.5,-0.5);
        	}else if(cycleCounter < 250){driveMotors(0,0); IntakeOnOff(0);
        	}
        	/*if(releaseToggle && cycleCounter > 275){ //Ball Release Sequence
        		if(cycleCounter > 275) cylinder.set(DoubleSolenoid.Value.kForward);
        		if(cycleCounter > 275 && cycleCounter < 300) IntakeOnOff(1);
        		else{IntakeOnOff(0);}//if(raiseAfterRelease && !returnAfter){ cylinder.set(DoubleSolenoid.Value.kReverse);}}
        	}*/
        	
        	//Updated Rock wall auto ball release sequence
        	if(cycleCounter>225 && releaseToggle && !returnAfter){
        		if(cycleCounter < 275){ cylinder.set(DoubleSolenoid.Value.kReverse);
        		}else if(cycleCounter < 345){ driveMotors(-0.5,-0.5); 
        		}else if(cycleCounter < 395){ cylinder.set(DoubleSolenoid.Value.kForward); driveMotors(0,0);
        		}else if(cycleCounter < 445){ IntakeOnOff(1);
        		}else if(cycleCounter < 470) IntakeOnOff(0);
        	}
        	
        	if(cycleCounter>225 && raiseAfterRelease && !returnAfter){
        	    if(cycleCounter < 275){ cylinder.set(DoubleSolenoid.Value.kReverse);
        	    }else if(cycleCounter<345){ driveMotors(.5,.5);
        		}else { driveMotors(0,0);}
        		
        	}
        	
        	//Updated Rock wall auto return sequence
        	if(cycleCounter>225 && returnAfter){
        		if(cycleCounter < 275){ cylinder.set(DoubleSolenoid.Value.kReverse);
        		}else if(cycleCounter < 325){ driveMotors(-0.2,-0.2); 
        		}else if(cycleCounter < 385){ cylinder.set(DoubleSolenoid.Value.kForward); driveMotors(0,0);
        		}else if(cycleCounter < 399){ driveMotors(-0.3,0.3); if(releaseToggle){IntakeOnOff(1);}
        		}else if(cycleCounter < 460){ driveMotors(0.7,-0.7); if(releaseToggle){IntakeOnOff(0);}else{IntakeOnOff(-.8);}
        		}else if(cycleCounter <510){ cylinder.set(DoubleSolenoid.Value.kReverse); driveMotors(0,0);
        		}else if(cycleCounter <560){ driveMotors(-0.2,-0.2);
        		}else if(cycleCounter < 585){ cylinder.set(DoubleSolenoid.Value.kForward); driveMotors(0,0);
        		}
        		
        	}
        	
        	//Arm Raising Sequence
        	//if(raiseAfterRelease && cycleCounter>300){ cylinder.set(DoubleSolenoid.Value.kForward);}
        	
        	/*if(returnAfter && cycleCounter > 300){ //Return Sequence
        		if(cycleCounter > 300 && cycleCounter < 320)driveMotors(-0.1,0.1);
        		else if(cycleCounter > 320 && cycleCounter < 335)driveMotors(0,0);
        		else if(cycleCounter > 335 && cycleCounter < 375)driveMotors(0.6,-0.6);
        		else if(cycleCounter > 375 && cycleCounter < 400)driveMotors(0.8,-0.8);
        	}*/
        	//if(cycleCounter < 150) driveMotors(-0.3, 0.3);
        	//else if(cycleCounter < 450) driveMotors(-0.8, 0.8);
        	//else if(cycleCounter < 600) cylinder.set(DoubleSolenoid.Value.kReverse);
        	//else if(cycleCounter < )
            // driveMotors(0,0);
        }else if(autoSelecter == 3.5){ // PORTCULLIS	
        	if(cycleCounter<100){cylinder.set(DoubleSolenoid.Value.kForward);
        	}else if(cycleCounter < 200){ driveMotors(-0.45,0.45);
        	}else if(cycleCounter < 250){ driveMotors(-0.7,0.7);
        	}else if(cycleCounter < 285){ driveMotors(0.4,-0.4);
        	}else if(cycleCounter < 315){ driveMotors(-0.7,0.7);
        	}
        	
        	if(releaseToggle && cycleCounter < 335 && cycleCounter >315){ IntakeOnOff(1);
        	}else {IntakeOnOff(0);}
        	
        	if(raiseAfterRelease && cycleCounter>320){ cylinder.set(DoubleSolenoid.Value.kReverse);}
        	
        	if(returnAfter && cycleCounter >370){
        		if(cycleCounter < 420){ driveMotors(-0.5,-0.5); 
        		}else if(cycleCounter < 475){ driveMotors(-0.45,0.45); 
        		}else if(cycleCounter < 500){ driveMotors(-0.7,0.7);
        		}else if(cycleCounter < 625){ driveMotors(0.4,-0.4);
        		}else if(cycleCounter < 650){ driveMotors(-0.6,0.6);
        		}else{driveMotors(0,0);}
        	}
        	
        }else if(autoSelecter == 4){ // MOAT
        	if(cycleCounter < 35) driveMotors(-0.3, 0.3);
        	else if(cycleCounter < 175) driveMotors(-0.75, 0.75);
        	else driveMotors(0,0);
        	
        	if(releaseToggle && cycleCounter > 165){ //Ball Release Sequence
        		if(cycleCounter > 165) cylinder.set(DoubleSolenoid.Value.kForward);
        		if(cycleCounter > 205 && cycleCounter < 230) IntakeOnOff(1);
        		else{IntakeOnOff(0);}// if(raiseAfterRelease){ cylinder.set(DoubleSolenoid.Value.kReverse);}}
        	}
        	//Arm Raising Sequence
        	if(raiseAfterRelease && cycleCounter>230){ cylinder.set(DoubleSolenoid.Value.kForward);}
        	
        	if(returnAfter && cycleCounter > 300){ //Return Sequence
        		if(cycleCounter > 300 && cycleCounter < 400) driveMotors(.7,-.7);
        		else driveMotors(0,0);
        	}
        	
        }else if(autoSelecter == 5){ // RAMPARTS
        	if(cycleCounter < 25){ driveMotors(-0.41, 0.4);       //Drive Forward Slow
        	}else if(cycleCounter < 75){ driveMotors(-0.75, 0.7);  //Drive Forward Fast
        	}else if(cycleCounter < 90){ driveMotors(-1,0);    //Turn to the left
        	}else if(cycleCounter < 135){ driveMotors(-0.78, 0.7); //Drive Forward Fast
        	}else driveMotors(0,0);                              //Stop
        	
        	if(releaseToggle && cycleCounter > 165){ //Ball Release Sequence
        		if(cycleCounter < 205) cylinder.set(DoubleSolenoid.Value.kForward);
        		if(cycleCounter < 230) IntakeOnOff(1);
        		else{IntakeOnOff(0);}// if(raiseAfterRelease) cylinder.set(DoubleSolenoid.Value.kReverse);}}
        	}
        	//Arm Raising Sequence
        	if(raiseAfterRelease && cycleCounter>230){ cylinder.set(DoubleSolenoid.Value.kReverse);}
        	
        }else if(autoSelecter ==4.9){
        	if(cycleCounter < turnTime*25){driveMotors(turnPower,turnPower);}
        	else{ driveMotors(0,0);}  
        }
        
        /*if (cycleCounter < 25) driveMotors(-1, 1);
        if (cycleCounter > 25) driveMotors(0, 0);
        */
        
        //driveMotors(cycleCounter/500,0);
        /*if (cycleCounter < 100) {
        	driveMotors(.5,0);
        }
        else if (cycleCounter < 200) {
        	driveMotors(0,0);
        }
        else if (cycleCounter < 300) {
        	driveMotors(-.5,0);
        }
        else if (cycleCounter < 400) {
        	driveMotors(0,0);
        }
        else if (cycleCounter < 500) {
        	driveMotors(.5,0);
        }*/
    }

    public void teleopInit() {
    	//cylinder.set(DoubleSolenoid.Value.kReverse);
    	System.out.println("Beginning teleop init");
        if (autonomousCommand != null) autonomousCommand.cancel();
        compressor.setClosedLoopControl(true);
        intakeCounter = 0;
        firstSpikeStarted = false;
        firstSpikeFinished = false;
        secondSpikeStarted = false;
        secondIntakeCounter = 0;
        
        //intakeUltrasonic = new AnalogInput(2);
        ultrasonicVoltage = 0;
        
        //leftTrim = SmartDashboard.getNumber("DB/Slider 2", 0.0);
        rightTrim = SmartDashboard.getNumber("DB/Slider 3", 1);
        if(rightTrim == 0){ SmartDashboard.putNumber("DB/Slider 3", 1); rightTrim = 1;}
        //rightTrim = 1;
        
        state = false;
        previousState = false;
        
        leftThreshold = 89;
        centerThreshold = 74;
        rightThreshold = 60;
        System.out.println("Completed teleop init");
    }
    	
    public void teleopPeriodic() {
        //System.out.println("Before: " + frontLeftDriveMotor.get());
        Scheduler.getInstance().run();
        intakePower = SmartDashboard.getNumber("DB/Slider 1",5);
        current = power.getCurrent(15);
        //ultrasonicVoltage = intakeUltrasonic.getAverageValue();
        
        //arcadeDrive(joyStickRight.getX(),joyStickLeft.getY());
        //arcadeDrive(logitech.getRawAxis(0),logitech.getRawAxis(5));
        
        
        arcadeDrive(-logitech.getRawAxis(1), logitech.getRawAxis(2));        //for driving/tank drive
        //driveMotors(joyStickLeft.getY()/1.2,-1*joyStickRight.getY()/1.2);
        //System.out.println("After: " + frontLeftDriveMotor.get());
        
        //checking for button values
        //change when button is ready for use
        //System.out.println(button.get());
        
        /*if(buttonA.get() != previousState) {
        	state = !state;
        	previousState = state;
        }*/
        
        //System.out.println(xbox.getPOV(0));
        
        
        //TODO: WTFBBQ
        //if(buttonY.get() || logitechLeftBumper.get()) cylinder.set(DoubleSolenoid.Value.kForward);
        //if(buttonA.get() || logitechRightBumper.get()) cylinder.set(DoubleSolenoid.Value.kReverse);
        //if(bumperRight.get() || bumperLeft.get()) stupidIntake = true;
        //if(xboxStartButton.get()) scalePistons.set(DoubleSolenoid.Value.kForward);
        //if(xboxBackButton.get()) scalePistons.set(DoubleSolenoid.Value.kReverse);
        //direction = xbox.getPOV(0);
        
        //logitechDirection= logitech.getPOV(1);

        	/*if (direction == 90 && direction == 270){
        		IntakeOnOff(0); intakeCounter = 0;
        	}else if (direction == 0){
        		IntakeOnOff(1); intakeCounter = 0;   //Out
        	}else if ((direction == 180 && intakeCounter < 25) || (current > 13 && intakeCounter < 25)){ 
        		IntakeOnOff(-1);   //IN
        		if (current > 13 && intakeCounter>15) intakeCounter++;
        	}else if(intakeCounter >= 25 && intakeCounter < 40) {IntakeOnOff(-.4); intakeCounter++;}
        	
        	else if(intakeCounter >= 40){IntakeOnOff(0); intakeCounter = 0;}*/
        
        
        /*   //Old Two Current Spike Detection  
        if (direction == 90 || direction == 270){
    		IntakeOnOff(0); intakeCounter = 0; firstSpikeStarted = false; firstSpikeFinished = false; secondSpikeStarted = false;
    		secondIntakeCounter = 0;
        }
        if (direction == 0){
    		IntakeOnOff(1); intakeCounter = 0; firstSpikeStarted = false; firstSpikeFinished = false; secondSpikeStarted = false;
    		secondIntakeCounter = 0;
    	}
        
        if(!stupidIntake){
        
        if(direction == 180) IntakeOnOff(-1);
        
        if(intake.get() < 0 && !firstSpikeFinished) intakeCounter++; 
        
        if(intake.get() < 0 && !firstSpikeStarted && current > 13) firstSpikeStarted = true; 
        
        if(firstSpikeStarted && intakeCounter>50) intakeCounter = 0; 
        
        if(firstSpikeStarted && intakeCounter == 0 ) firstSpikeFinished = true;
        
        if(firstSpikeFinished && current > 20) {secondSpikeStarted = true; secondIntakeCounter++;}
        
        if(secondSpikeStarted && secondIntakeCounter > 2) IntakeOnOff(intakePower/-5);
        
        if(secondSpikeStarted && secondIntakeCounter > 35){
        	IntakeOnOff(0);
        	firstSpikeStarted = false;
        	firstSpikeFinished = false;
        	secondSpikeStarted = false;
        	intakeCounter = 0;
        	secondIntakeCounter = 0;
        }
        }else if(direction == 180) IntakeOnOff(-1);
        */
        /*
        if(ultrasonicVoltage >= leftThreshold){
        	SmartDashboard.putString("DB/String 0", "No Ball");
        }if(ultrasonicVoltage < rightThreshold){
        	SmartDashboard.putString("DB/String 0", "Right");
        }if(ultrasonicVoltage >= rightThreshold && ultrasonicVoltage < centerThreshold){
        	SmartDashboard.putString("DB/String 0", "Center");
        }if(ultrasonicVoltage < leftThreshold && ultrasonicVoltage >= centerThreshold){
        	SmartDashboard.putString("DB/String 0", "Left");
        }*/
        
        if (ultrasonicVoltage >= 90) {
        	SmartDashboard.putString("DB/String 0", "No Ball");
        }if (ultrasonicVoltage < 90) {
        	SmartDashboard.putString("DB/String 0", "Ball-in");
        }
        
        System.out.println("Ultrasonic Sensor Voltage:" + ultrasonicVoltage);
        //System.out.println("Ultrasonic Sensor Average Value:" + intakeUltrasonic.getAverageValue());

        if(direction==90 || direction==270 || logitechX.get()|| logitechB.get()){
        	intakeCounter = 0; secondIntakeCounter=0;
        	IntakeOnOff(0);
        }else if(direction == 0 || logitechY.get()){
        	intakeCounter = 0; secondIntakeCounter=0;
        	IntakeOnOff(1);
        }else if(direction == 180 ||logitechA.get()){
        	intakeCounter++;
        	IntakeOnOff(-1);
        }else if(intakeCounter>0){
        	intakeCounter++;
        	IntakeOnOff(-1);
        }
        
        if(current > 20 && intake.get()==-1){
        	secondIntakeCounter++;
        }
        if(intakeCounter>50 && secondIntakeCounter>35){
        	intakeCounter = 0; secondIntakeCounter=0;
        	IntakeOnOff(0);
        }
        
        
        //if (current > 13 && intake.get() == -1 && intakeCounter > 30 ) IntakeOnOff(0); intakeCounter = 0;
        
        /*if(xbox.getPOV(0)==90 || xbox.getPOV(0)==270) IntakeOnOff(0);
        else if(xbox.getPOV(0)==0) IntakeOnOff(-1);
        else if(xbox.getPOV(0)==180) IntakeOnOff(1);*/
        
        /*if (buttonA.get()){
        	IntakeOnOff(-1);
        }
        if (buttonY.get()){
        	IntakeOnOff(1);
        }
        if(!buttonA.get() && !buttonY.get()){
        	IntakeOnOff(0);
        }*/
        
        operatorControl();
        //System.out.println("After 2: " + frontLeftDriveMotor.get());
        
        System.out.println(current);
        current = power.getCurrent(15);
        
        //System.out.println("Memes (a tribute to Sam meme master sidhu)");
    }
    
    
    public void testPeriodic() {
        LiveWindow.run();
        
        
    }
}

