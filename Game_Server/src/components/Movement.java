package components;

import org.lwjgl.util.vector.Vector3f;

import networking.Server;

public class Movement extends Component{
	
	public static final float GRAVITY = -50;
	public static final float UP_FORCE = 27;
	
	private float currentSpeed;
	private float currentTurnSpeed;
	private float jumpSpeed;
	
	private boolean airborne;
	
	private Vector3f currentVelocity;

	public Movement() {
		super("movement_comp");
		init();
	}

	@Override
	protected void init() {
		currentSpeed = 0;
		currentTurnSpeed = 0;
		jumpSpeed = 0;
		currentVelocity = new Vector3f(0,0,0);
		airborne = false;
	}

	@Override
	public void udpate() {
		
	}

	@Override
	public void start() {
		
	}
	
	public void jump()
	{
		if(!airborne)
		{
			airborne = true;
			jumpSpeed = UP_FORCE;
		}
	}
	
	public void applyGravity()
	{
		jumpSpeed += GRAVITY * Server.getDeltaTime();
	}

	public float getCurrentSpeed()
	{
		return currentSpeed;
	}
	
	public void setCurrentSpeed(float speed)
	{
		this.currentSpeed = speed;
	}

	public float getCurrentTurnSpeed() {
		return currentTurnSpeed;
	}

	public void setCurrentTurnSpeed(float currentTurnSpeed) {
		this.currentTurnSpeed = currentTurnSpeed;
	}

	public Vector3f getCurrentVelocity() {
		return currentVelocity;
	}

	public void setCurrentVelocity(Vector3f currentVelocity) {
		this.currentVelocity = currentVelocity;
	}

	public float getJumpSpeed() {
		return jumpSpeed;
	}

	public void setJumpSpeed(float jumpSpeed) {
		this.jumpSpeed = jumpSpeed;
	}

	public boolean isAirborne() {
		return airborne;
	}

	public void setAirborne(boolean airborne) {
		this.airborne = airborne;
	}
	
	
	
	
	
	
	
	
	
	

}
