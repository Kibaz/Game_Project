package components;

import org.lwjgl.util.vector.Vector3f;

import rendering.Window;

public class Motion extends Component {
	
	private static final float GRAVITY = -50;
	private static final float TURN_SPEED = 160;
	private static final float UP_FORCE = 27;
	
	
	private float runSpeed;
	private float walkSpeed;
	private float swimSpeed;
	
	private float jumpSpeed;
	
	private float currentSpeed;
	private float currentTurnSpeed;
	
	private boolean airborne;
	
	private Vector3f currentVelocity;
	
	public Motion()
	{
		super("movement");
		init();
	}

	@Override
	protected void init() {
		runSpeed = 0;
		walkSpeed = 0;
		swimSpeed = 0;
		jumpSpeed = 0;
		currentSpeed = 0;
		currentTurnSpeed = 0;
		currentVelocity = new Vector3f(0,0,0);
		airborne = false;
	}

	@Override
	public void update() {
		if(entity != null)
		{
			
		}
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void cleanUp() {
		// TODO Auto-generated method stub
		
	}

	public float getRunSpeed() {
		return runSpeed;
	}

	public void setRunSpeed(float runSpeed) {
		this.runSpeed = runSpeed;
	}

	public float getWalkSpeed() {
		return walkSpeed;
	}

	public void setWalkSpeed(float walkSpeed) {
		this.walkSpeed = walkSpeed;
	}

	public float getSwimSpeed() {
		return swimSpeed;
	}

	public void setSwimSpeed(float swimSpeed) {
		this.swimSpeed = swimSpeed;
	}

	public float getJumpSpeed() {
		return jumpSpeed;
	}

	public void setJumpSpeed(float jumpSpeed) {
		this.jumpSpeed = jumpSpeed;
	}

	public static float getGravity() {
		return GRAVITY;
	}

	public static float getTurnSpeed() {
		return TURN_SPEED;
	}

	public float getCurrentSpeed() {
		return currentSpeed;
	}

	public void setCurrentSpeed(float currentSpeed) {
		this.currentSpeed = currentSpeed;
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

	public void setCurrentVelocity(float x, float y, float z) {
		currentVelocity.set(x, y, z);
	}

	public boolean isAirborne()
	{
		return airborne;
	}
	
	public void setAirborne(boolean airborne)
	{
		this.airborne = airborne;
	}

	public static float getUpForce() {
		return UP_FORCE;
	}
	
	
	
	
	
	
	

}
