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
    Joystick xbox;
    Joystick logitech;
    
    JoystickButton buttonA;
    JoystickButton buttonY;
    JoystickButton buttonB;
    JoystickButton bumperRight;
    JoystickButton bumperLeft;
    
    Victor frontLeftDriveMotor;
    Victor frontRightDriveMotor;
    Victor backLeftDriveMotor;
    Victor backRightDriveMotor;
    Victor intake;
    
    double intakePower;
    
    double autoSelecter;
    boolean releaseToggle;
    boolean raiseAfterRelease;
    boolean returnAfter;
        
    int cycleCounter;
    int intakeCounter;
    
    //Global Speed Values
    double leftSpeed;
    double rightSpeed;
    
    double throttleGain;
    double turningGain;
    
    double current;
    
    //A cylinder
    DoubleSolenoid cylinder;
    
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
       frontLeftDriveMotor = new Victor(0);
       frontRightDriveMotor = new Victor(2);
       backLeftDriveMotor = new Victor(1); 
       backRightDriveMotor = new Victor(3);
       
       intake = new Victor(4);
       
       //joyStickLeft = new Joystick(0);
       //joyStickRight = new Joystick(1);
       logitech = new Joystick(2);
       xbox = new Joystick(1);
       
       
       buttonA = new JoystickButton(xbox, 1);
       buttonY = new JoystickButton(xbox, 4);
       buttonB = new JoystickButton(xbox, 2);
       bumperRight = new JoystickButton(xbox, 6);
       bumperLeft = new JoystickButton(xbox, 5);
       
       cylinder = new DoubleSolenoid(0,1);
       
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
        releaseToggle = SmartDashboard.getBoolean("DB/Button 0", false);
        raiseAfterRelease = SmartDashboard.getBoolean("DB/Button 1", false);
        returnAfter = SmartDashboard.getBoolean("DB/Button 2",false);
        System.out.println(autoSelecter);
        System.out.println(releaseToggle);
        System.out.println(raiseAfterRelease);
        System.out.println(returnAfter);
        //if(autoSelecter == 0) autoRoutine=0;
        
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
        	if(cycleCounter < 100) driveMotors(-0.0, 0.0);
        	else if(cycleCounter < 275) driveMotors(-0.4, 0.4);
        	else driveMotors(0,0);
        	if(releaseToggle){
        		//if(cycleCounter > 450) cylinder.set(DoubleSolenoid.Value.kReverse);
        		if(cycleCounter > 275 && cycleCounter < 300) IntakeOnOff(1);
        		else IntakeOnOff(0); if(raiseAfterRelease) cylinder.set(DoubleSolenoid.Value.kForward);
        	}
        	if(returnAfter){
        		if(cycleCounter > 300 && cycleCounter < 475) driveMotors(.4,-.4);
        		else driveMotors(0,0);
        	}
        	
        }else if(autoSelecter == 2){ // ROUGH TERRAIN
        	if(cycleCounter < 50) driveMotors(-0.3, 0.3);
        	else if(cycleCounter < 150) driveMotors(-0.7, 0.7);
        	else driveMotors(0,0); 
        	
        	if(releaseToggle){
        		if(cycleCounter > 165) cylinder.set(DoubleSolenoid.Value.kReverse);
        		if(cycleCounter > 205 && cycleCounter < 230) IntakeOnOff(1);
        		else IntakeOnOff(0); if(raiseAfterRelease) cylinder.set(DoubleSolenoid.Value.kReverse);
        	}
        	if(returnAfter && releaseToggle && raiseAfterRelease){
        		if(cycleCounter > 300 && cycleCounter < 475) driveMotors(.6,-.6);
        		else driveMotors(0,0);
        	}
        	
        }else if(autoSelecter == 3){ // ROCKWALL
        	
        	if(cycleCounter < 50) driveMotors(-0.3, 0.3);
        	else if(cycleCounter < 150) driveMotors(-0.8, 0.8);
        	else driveMotors(0,0);
        	
        	if(releaseToggle){
        		if(cycleCounter > 165) cylinder.set(DoubleSolenoid.Value.kForward);
        		if(cycleCounter > 205 && cycleCounter < 230) IntakeOnOff(1);
        		else IntakeOnOff(0);if(raiseAfterRelease) cylinder.set(DoubleSolenoid.Value.kReverse);
        	}
        	
        	//if(cycleCounter < 150) driveMotors(-0.3, 0.3);
        	//else if(cycleCounter < 450) driveMotors(-0.8, 0.8);
        	//else if(cycleCounter < 600) cylinder.set(DoubleSolenoid.Value.kReverse);
        	//else if(cycleCounter < )
            // driveMotors(0,0);
        }else if(autoSelecter == 4){ // MOAT
        	if(cycleCounter < 50) driveMotors(-0.3, 0.3);
        	else if(cycleCounter < 150) driveMotors(-0.7, 0.7);
        	else driveMotors(0,0);
        	
        	if(releaseToggle){
        		if(cycleCounter > 165) cylinder.set(DoubleSolenoid.Value.kForward);
        		if(cycleCounter > 205 && cycleCounter < 230) IntakeOnOff(1);
        		else IntakeOnOff(0); if(raiseAfterRelease) cylinder.set(DoubleSolenoid.Value.kReverse);
        	}
        	if(returnAfter && releaseToggle && raiseAfterRelease){
        		if(cycleCounter > 300 && cycleCounter < 475) driveMotors(.7,-.7);
        		else driveMotors(0,0);
        	}
        	
        }else if(autoSelecter == 5){ // RAMPARTS
        	if(cycleCounter < 25) driveMotors(-0.4, 0.4);     //Drive Forward Slow
        	else if(cycleCounter < 75) driveMotors(-0.7, 0.7); //Drive Forward Fast
        	else if(cycleCounter < 90) driveMotors(-1, 0.2); //Turn to the left
        	else if(cycleCounter < 150) driveMotors(-0.7, 0.7); //Drive Forward Fast
        	else driveMotors(0,0);                             //Stop
        	
        	if(releaseToggle){
        		if(cycleCounter > 165) cylinder.set(DoubleSolenoid.Value.kForward);
        		if(cycleCounter > 205 && cycleCounter < 230) IntakeOnOff(1);
        		else IntakeOnOff(0); if(raiseAfterRelease) cylinder.set(DoubleSolenoid.Value.kReverse);
        	}
        	
        	
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
    	cylinder.set(DoubleSolenoid.Value.kReverse);
        if (autonomousCommand != null) autonomousCommand.cancel();
        compressor.setClosedLoopControl(true);
        intakeCounter = 0;
        
        state = false;
        previousState = false;
    }
    	
    public void teleopPeriodic() {
        //System.out.println("Before: " + frontLeftDriveMotor.get());
        Scheduler.getInstance().run();
        intakePower = SmartDashboard.getNumber("DB/Slider 1",5);
        current = power.getCurrent(15);
        
        
        //arcadeDrive(joyStickRight.getX(),joyStickLeft.getY());
        //arcadeDrive(logitech.getRawAxis(0),logitech.getRawAxis(5));
        arcadeDrive(logitech.getRawAxis(0)+(logitech.getRawAxis(3)-logitech.getRawAxis(2)),logitech.getRawAxis(5));
        
        //for driving/tank drive
        //driveMotors(joyStickLeft.getY()/1.2,-1*joyStickRight.getY()/1.2);
        //System.out.println("After: " + frontLeftDriveMotor.get());
        
        //checking for button values
        //change when button is ready for use
        //System.out.println(button.get());
        /*if(buttonA.get() != previousState) {
        	state = !state;
        	previousState = state;
        }*/
        
        System.out.println(xbox.getPOV(0));
        
        
        
        if(buttonY.get()) cylinder.set(DoubleSolenoid.Value.kForward);
        if(buttonA.get()) cylinder.set(DoubleSolenoid.Value.kReverse);
        
        int direction = xbox.getPOV(0);
        
        if (direction != -1){
        	if (direction == 0) IntakeOnOff(1);                        //Out
        	//if (direction == 0 && cycleCounter < 60) IntakeOnOff(1);                        //Out
        	else if (direction == 180 && (current < 13 || intakeCounter < 25)) IntakeOnOff(-1);//In and turn off at 30 amps
        	//else if (direction == 180 && (current < 13 || (intakeCounter >= 25 && intakeCounter < 40))) IntakeOnOff(intakePower);
        	else IntakeOnOff(0); intakeCounter = 0;                    //Off
        }
        
        if (current > 13 && intake.get() == -1 && intakeCounter > 30 ) IntakeOnOff(0); intakeCounter = 0;
        intakeCounter++;
        
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
    }
    
    
    public void testPeriodic() {
        LiveWindow.run();
        
        
    }
}package org.usfirst.frc.team5811.robot;



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
    Joystick xbox;
    Joystick logitech;
    
    JoystickButton buttonA;
    JoystickButton buttonY;
    JoystickButton buttonB;
    JoystickButton bumperRight;
    JoystickButton bumperLeft;
    
    Victor frontLeftDriveMotor;
    Victor frontRightDriveMotor;
    Victor backLeftDriveMotor;
    Victor backRightDriveMotor;
    Victor intake;
    
    double intakePower;
    
    double autoSelecter;
    boolean releaseToggle;
    boolean raiseAfterRelease;
    boolean returnAfter;
        
    int cycleCounter;
    int intakeCounter;
    
    //Global Speed Values
    double leftSpeed;
    double rightSpeed;
    
    double throttleGain;
    double turningGain;
    
    double current;
    
    //A cylinder
    DoubleSolenoid cylinder;
    
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
       frontLeftDriveMotor = new Victor(0);
       frontRightDriveMotor = new Victor(2);
       backLeftDriveMotor = new Victor(1); 
       backRightDriveMotor = new Victor(3);
       
       intake = new Victor(4);
       
       //joyStickLeft = new Joystick(0);
       //joyStickRight = new Joystick(1);
       logitech = new Joystick(2);
       xbox = new Joystick(1);
       
       
       buttonA = new JoystickButton(xbox, 1);
       buttonY = new JoystickButton(xbox, 4);
       buttonB = new JoystickButton(xbox, 2);
       bumperRight = new JoystickButton(xbox, 6);
       bumperLeft = new JoystickButton(xbox, 5);
       
       cylinder = new DoubleSolenoid(0,1);
       
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
        releaseToggle = SmartDashboard.getBoolean("DB/Button 0", false);
        raiseAfterRelease = SmartDashboard.getBoolean("DB/Button 1", false);
        returnAfter = SmartDashboard.getBoolean("DB/Button 2",false);
        System.out.println(autoSelecter);
        System.out.println(releaseToggle);
        System.out.println(raiseAfterRelease);
        System.out.println(returnAfter);
        //if(autoSelecter == 0) autoRoutine=0;
        
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
        	if(cycleCounter < 100) driveMotors(-0.0, 0.0);
        	else if(cycleCounter < 275) driveMotors(-0.4, 0.4);
        	else driveMotors(0,0);
        	if(releaseToggle){
        		//if(cycleCounter > 450) cylinder.set(DoubleSolenoid.Value.kReverse);
        		if(cycleCounter > 275 && cycleCounter < 300) IntakeOnOff(1);
        		else IntakeOnOff(0); if(raiseAfterRelease) cylinder.set(DoubleSolenoid.Value.kForward);
        	}
        	if(returnAfter){
        		if(cycleCounter > 300 && cycleCounter < 475) driveMotors(.4,-.4);
        		else driveMotors(0,0);
        	}
        	
        }else if(autoSelecter == 2){ // ROUGH TERRAIN
        	if(cycleCounter < 50) driveMotors(-0.3, 0.3);
        	else if(cycleCounter < 150) driveMotors(-0.7, 0.7);
        	else driveMotors(0,0); 
        	
        	if(releaseToggle){
        		if(cycleCounter > 165) cylinder.set(DoubleSolenoid.Value.kReverse);
        		if(cycleCounter > 205 && cycleCounter < 230) IntakeOnOff(1);
        		else IntakeOnOff(0); if(raiseAfterRelease) cylinder.set(DoubleSolenoid.Value.kReverse);
        	}
        	if(returnAfter && releaseToggle && raiseAfterRelease){
        		if(cycleCounter > 300 && cycleCounter < 475) driveMotors(.6,-.6);
        		else driveMotors(0,0);
        	}
        	
        }else if(autoSelecter == 3){ // ROCKWALL
        	
        	if(cycleCounter < 50) driveMotors(-0.3, 0.3);
        	else if(cycleCounter < 150) driveMotors(-0.8, 0.8);
        	else driveMotors(0,0);
        	
        	if(releaseToggle){
        		if(cycleCounter > 165) cylinder.set(DoubleSolenoid.Value.kForward);
        		if(cycleCounter > 205 && cycleCounter < 230) IntakeOnOff(1);
        		else IntakeOnOff(0);if(raiseAfterRelease) cylinder.set(DoubleSolenoid.Value.kReverse);
        	}
        	
        	//if(cycleCounter < 150) driveMotors(-0.3, 0.3);
        	//else if(cycleCounter < 450) driveMotors(-0.8, 0.8);
        	//else if(cycleCounter < 600) cylinder.set(DoubleSolenoid.Value.kReverse);
        	//else if(cycleCounter < )
            // driveMotors(0,0);
        }else if(autoSelecter == 4){ // MOAT
        	if(cycleCounter < 50) driveMotors(-0.3, 0.3);
        	else if(cycleCounter < 150) driveMotors(-0.7, 0.7);
        	else driveMotors(0,0);
        	
        	if(releaseToggle){
        		if(cycleCounter > 165) cylinder.set(DoubleSolenoid.Value.kForward);
        		if(cycleCounter > 205 && cycleCounter < 230) IntakeOnOff(1);
        		else IntakeOnOff(0); if(raiseAfterRelease) cylinder.set(DoubleSolenoid.Value.kReverse);
        	}
        	if(returnAfter && releaseToggle && raiseAfterRelease){
        		if(cycleCounter > 300 && cycleCounter < 475) driveMotors(.7,-.7);
        		else driveMotors(0,0);
        	}
        	
        }else if(autoSelecter == 5){ // RAMPARTS
        	if(cycleCounter < 25) driveMotors(-0.4, 0.4);     //Drive Forward Slow
        	else if(cycleCounter < 75) driveMotors(-0.7, 0.7); //Drive Forward Fast
        	else if(cycleCounter < 90) driveMotors(-1, 0.2); //Turn to the left
        	else if(cycleCounter < 150) driveMotors(-0.7, 0.7); //Drive Forward Fast
        	else driveMotors(0,0);                             //Stop
        	
        	if(releaseToggle){
        		if(cycleCounter > 165) cylinder.set(DoubleSolenoid.Value.kForward);
        		if(cycleCounter > 205 && cycleCounter < 230) IntakeOnOff(1);
        		else IntakeOnOff(0); if(raiseAfterRelease) cylinder.set(DoubleSolenoid.Value.kReverse);
        	}
        	
        	
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
    	cylinder.set(DoubleSolenoid.Value.kReverse);
        if (autonomousCommand != null) autonomousCommand.cancel();
        compressor.setClosedLoopControl(true);
        intakeCounter = 0;
        
        state = false;
        previousState = false;
    }
    	
    public void teleopPeriodic() {
        //System.out.println("Before: " + frontLeftDriveMotor.get());
        Scheduler.getInstance().run();
        intakePower = SmartDashboard.getNumber("DB/Slider 1",5);
        current = power.getCurrent(15);
        
        
        //arcadeDrive(joyStickRight.getX(),joyStickLeft.getY());
        //arcadeDrive(logitech.getRawAxis(0),logitech.getRawAxis(5));
        arcadeDrive(logitech.getRawAxis(0)+(logitech.getRawAxis(3)-logitech.getRawAxis(2)),logitech.getRawAxis(5));
        
        //for driving/tank drive
        //driveMotors(joyStickLeft.getY()/1.2,-1*joyStickRight.getY()/1.2);
        //System.out.println("After: " + frontLeftDriveMotor.get());
        
        //checking for button values
        //change when button is ready for use
        //System.out.println(button.get());
        /*if(buttonA.get() != previousState) {
        	state = !state;
        	previousState = state;
        }*/
        
        System.out.println(xbox.getPOV(0));
        
        
        
        if(buttonY.get()) cylinder.set(DoubleSolenoid.Value.kForward);
        if(buttonA.get()) cylinder.set(DoubleSolenoid.Value.kReverse);
        
        int direction = xbox.getPOV(0);
        
        if (direction != -1){
        	if (direction == 0) IntakeOnOff(1);                        //Out
        	//if (direction == 0 && cycleCounter < 60) IntakeOnOff(1);                        //Out
        	else if (direction == 180 && (current < 13 || intakeCounter < 25)) IntakeOnOff(-1);//In and turn off at 30 amps
        	//else if (direction == 180 && (current < 13 || (intakeCounter >= 25 && intakeCounter < 40))) IntakeOnOff(intakePower);
        	else IntakeOnOff(0); intakeCounter = 0;                    //Off
        }
        
        if (current > 13 && intake.get() == -1 && intakeCounter > 30 ) IntakeOnOff(0); intakeCounter = 0;
        intakeCounter++;
        
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
    }
    
    
    public void testPeriodic() {
        LiveWindow.run();
        
        
    }
}
