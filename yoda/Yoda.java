package yoda;
import robocode.*;
import java.awt.*;


/**
 * Yoda - a robot by Liviu Petre
 */
public class Yoda extends AdvancedRobot
{
    int botDirection=1;//which way to move
	int bulletHit = 1;
	double maxFirePower = 3.1;
	double tooFarLimit = 130;//150
	double tooCloseLimit = 50;
	double moveWhenTooClose = 100;
	int useBulletHit = 1;
	int minCorrection = 17;
	double enemyPosition;
    double latVel;
    double gunTurnAmt;
	double moveAheadDistance;

    public void run() {
        setAdjustRadarForRobotTurn(true);
		setAdjustGunForRobotTurn(true);
		
        setBodyColor(new Color(147, 140, 143));
        setGunColor(new Color(47,249,36));
        setRadarColor(new Color(156, 187, 128));
        setScanColor(new Color(156, 187, 128));
        setBulletColor(new Color(47,249,36));
       
	    turnRadarRightRadians(Double.POSITIVE_INFINITY);//keep turning radar right

		while (true) {
			// rotate the radar
			if (getRadarTurnRemainingRadians() == 0) {
				setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
			} 
			setTurnRight(5);
			setAhead(30 * botDirection);
			execute();
		}
    }


    public void onScannedRobot(ScannedRobotEvent e) {
        
        setTurnRadarLeftRadians(getRadarTurnRemainingRadians());
        if(Math.random()>.8) setMaxVelocity(randomizeNumber(9,1)+3);
        
		//aim
		enemyPosition = e.getBearingRadians()+ getHeadingRadians();
        latVel = e.getVelocity() * Math.sin(e.getHeadingRadians() - enemyPosition);
        gunTurnAmt = randomizeNumber(robocode.util.Utils.normalRelativeAngle(enemyPosition - getGunHeadingRadians()+ latVel /(minCorrection + getVelocity() / 40)), 0.03);//amount to turn our gun
		setTurnGunRightRadians(optimizeAngle(gunTurnAmt)); 

		//turn
		if (e.getDistance() > tooFarLimit) {//turn
		 	setTurnRightRadians(randomizeNumber(optimizeAngle(robocode.util.Utils.normalRelativeAngle(e.getBearingRadians()+latVel/getVelocity())),0.2));
		}
		else setTurnLeft(randomizeNumber(-90-e.getBearing(),0.1)); 
		
		//move
        if (e.getDistance() > tooFarLimit) { 
			useBulletHit = 1; 
			moveAheadDistance = 90;
		}
        else if (e.getDistance() > tooCloseLimit) {
			useBulletHit = bulletHit; 
			moveAheadDistance = 40;
		}
		else {
			useBulletHit = 1; 
			moveAheadDistance = 10;
		}
		if (e.getDistance() > tooCloseLimit) { 
			setAhead(randomizeNumber((e.getDistance() - moveAheadDistance)*botDirection*useBulletHit,0.1));
		}
		else setAhead(randomizeNumber(moveWhenTooClose*botDirection,0.1));
		
		//fire
		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {	
			setFire(Math.min(400 / e.getDistance(), maxFirePower+15/e.getDistance()));
		}
			
    }


    public void onHitWall(HitWallEvent e){
        botDirection = -botDirection; //change direction when hitting wall
	    }
	
	public void onHitRobot(HitRobotEvent e) { 
		if (getVelocity() == 0) botDirection = -botDirection; //change direction when hitting robot, only if blocked
	}
	
	public void onHitByBullet(HitByBulletEvent e){
		bulletHit = -bulletHit; //change direction when hit by bullet, to dodge
	}
	
    public void onWin(WinEvent e) {
        for (int i = 0; i < 100; i++) { //dance!!!
            setTurnRight(60);
            if (i%4==0) setTurnLeft(60);
        }
    }
	
	public double optimizeAngle(double angle) { //choose the shortest turn
		double v = Math.PI;
		if (angle >  v) angle -= 2*v; 
		if (angle < -v) angle += 2*v;
		return angle;
	}
	
	public double randomizeNumber (double n, double p) {
		return n+n*(Math.random()-0.5)*p*2; //generate a number between n+-n*p, p<=1
	}
}
