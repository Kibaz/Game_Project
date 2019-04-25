package rendering;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import animation.AnimatedCharacter;
import entities.Camera;
import entities.Entity;
import entities.Light;
import lwjglToAWT.AWTGLCanvas;
import lwjglToAWT.GLData;
import models.BaseModel;
import models.TexturedModel;
import terrains.Terrain;
import texturing.ModelTexture;
import utils.OBJLoader;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JToolBar;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.Color;
import javax.swing.UIManager;


public class Window extends JFrame implements MouseListener,MouseMotionListener,MouseWheelListener{
	
	public static final int WIDTH = 1600;
	public static final int HEIGHT = 900;
	
	private static long lastTimeInterval;
	
	private static float deltaTime;
	
	private static boolean mouseButtons[] = new boolean[1024];
	
	private float lastMouseX;
	private float lastMouseY;
	private static float mouseDX;
	private static float mouseDY;
	
	private Map<Integer,Object[]> entityQueue;
	
	private List<Terrain> terrains = new ArrayList<>();
	private List<Light> lights = new ArrayList<>();
	private List<Entity> entities = new ArrayList<>();
	private List<AnimatedCharacter> animChars = new ArrayList<>();
	
	private AdvancedRenderer renderer;
	
	private Camera camera;
	
	private Loader loader;
	private JTextField entObjFile;
	private JTextField entTextureFile;
	
	public Window(Loader loader)
	{
		this.loader = loader;
		camera = new Camera();
		entityQueue = new HashMap<>();
		init();
	}
	
	private void init()
	{
		setTitle("World Editor");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		setPreferredSize(new Dimension(WIDTH,HEIGHT));
		GLData glData = new GLData();
		glData.samples = 4;
		glData.swapInterval = 0;
		AWTGLCanvas glCanvas;
		Loader loader = new Loader();
		getContentPane().add(glCanvas = new AWTGLCanvas(glData) {

			private static final long serialVersionUID = 1L;

			@Override
			public void initGL() {
				// Use to initialise OpenGL
				GL.createCapabilities();
				/*ModelTexture terrainTex = new ModelTexture(loader.loadTexture("res/Brown.png"));
				terrainTex.setShineDamper(1);
				terrainTex.setReflectivity(0);
				terrains.add(new Terrain(0,0,loader,terrainTex,"heightmap"));*/
				renderer = new AdvancedRenderer(loader,camera);
			}

			@Override
			public void paintGL() {
				// Use as rendering loop
				Iterator<Integer> it = entityQueue.keySet().iterator();
				while(it.hasNext())
				{
					int currIndex = it.next();
					Object[] data = entityQueue.get(currIndex);
					BaseModel test = OBJLoader.loadObj((String) data[0], loader);
					ModelTexture texture = new ModelTexture(loader.loadTexture((String) data[1]));
					TexturedModel texModel = new TexturedModel(test,texture);
					Entity entity = new Entity(texModel,new Vector3f((float) data[2],(float) data[3],(float) data[4]),(float) data[5],(float) data[6],(float) data[7],(float) data[8]);
					entities.add(entity);
					it.remove();
				}
				camera.move();
				renderer.renderScene(entities, terrains, animChars, lights, camera, new Vector4f(0,-1f,0,0));
				
				
				Window.update();
				swapBuffers();
			}
			
		}, BorderLayout.CENTER);
		
		JPanel entityCreator = new JPanel();
		entityCreator.setBackground(Color.DARK_GRAY);
		Border entityPanelBorder = BorderFactory.createTitledBorder("Entity Creator");
		((TitledBorder) entityPanelBorder).setTitleJustification(TitledBorder.CENTER);
		getContentPane().add(entityCreator, BorderLayout.EAST);
		entityCreator.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Entity Creator", TitledBorder.CENTER, TitledBorder.TOP, null, Color.WHITE));
		entityCreator.setVisible(false);
		
		JLabel lblModel = new JLabel("Model:");
		lblModel.setForeground(Color.WHITE);
		
		entObjFile = new JTextField();
		entObjFile.setEditable(false);
		entObjFile.setColumns(10);
		
		JButton browseObjFiles = new JButton("Browse");
		browseObjFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser objChooser = new JFileChooser();
				FileNameExtensionFilter objFilter = new FileNameExtensionFilter("OBJ Files", "obj");
				objChooser.setAcceptAllFileFilterUsed(false);
				objChooser.setFileFilter(objFilter);
				int returnVal = objChooser.showOpenDialog(browseObjFiles);
				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					File file = objChooser.getSelectedFile();
					entObjFile.setText(file.getPath());
				}
			}
		});
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				entityCreator.setVisible(false);
				
			}
			
		});
		
		JLabel lblTexture = new JLabel("Texture:");
		lblTexture.setForeground(Color.WHITE);
		
		entTextureFile = new JTextField();
		entTextureFile.setEditable(false);
		entTextureFile.setColumns(10);
		
		JButton browseTextures = new JButton("Browse");
		browseTextures.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser imgChooser = new JFileChooser();
				FileNameExtensionFilter imgFilter = new FileNameExtensionFilter("Texture Files", "jpg", "png");
				imgChooser.setAcceptAllFileFilterUsed(false);
				imgChooser.setFileFilter(imgFilter);
				int returnVal = imgChooser.showOpenDialog(browseObjFiles);
				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					File file = imgChooser.getSelectedFile();
					entTextureFile.setText(file.getPath());
				}
				
			}
			
		});
		
		JLabel lblPositionX = new JLabel("Position X:");
		lblPositionX.setForeground(Color.WHITE);
		
		JLabel lblPositionY = new JLabel("Position Y:");
		lblPositionY.setForeground(Color.WHITE);
		
		JLabel lblPositionZ = new JLabel("Position Z:");
		lblPositionZ.setForeground(Color.WHITE);
		
		JLabel lblRotationX = new JLabel("Rotation X:");
		lblRotationX.setForeground(Color.WHITE);
		
		JLabel lblRotationY = new JLabel("Rotation Y:");
		lblRotationY.setForeground(Color.WHITE);
		
		JLabel lblRotationZ = new JLabel("Rotation Z:");
		lblRotationZ.setForeground(Color.WHITE);
		
		JSpinner posXField = new JSpinner();
		posXField.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(1)));
		
		JSpinner posYField = new JSpinner();
		posYField.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(1)));
		
		JSpinner posZField = new JSpinner();
		posZField.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(1)));
		
		JSpinner rotXField = new JSpinner();
		rotXField.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(1)));
		
		JSpinner rotYField = new JSpinner();
		rotYField.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(1)));
		
		JSpinner rotZField = new JSpinner();
		rotZField.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(1)));
		
		JLabel lblScale = new JLabel("Scale:");
		lblScale.setForeground(Color.WHITE);
		
		JSpinner scaleField = new JSpinner();
		scaleField.setModel(new SpinnerNumberModel(new Float(1), new Float(1), null, new Float(1)));
		
		JButton createEntityBtn = new JButton("Create");
		createEntityBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Ensure that a model and texture file have been selected
				if(!entObjFile.getText().equals("")&&
						!entTextureFile.getText().equals(""))
				{
					Border defaultBorder = (Border) entObjFile.getClientProperty("border");
					if(defaultBorder != null)
					{
						entObjFile.setBorder(defaultBorder);
						entTextureFile.setBorder(defaultBorder);
					}
					
					String objPath = entObjFile.getText(); // OBJ File
					String texturePath = entTextureFile.getText(); // Texture File
					Object posX = posXField.getValue();
					Object posY = posYField.getValue();
					Object posZ = posZField.getValue();
					Object rotX = rotXField.getValue();
					Object rotY = rotYField.getValue();
					Object rotZ = rotZField.getValue();
					Object scale = scaleField.getValue();
					
					// Register model path and texture path in queue
					entityQueue.put(entityQueue.size()+1, new Object[] {
							objPath,texturePath,posX,posY,posZ,rotX,rotY,rotZ,scale
					});
					
				}
				else
				{
					entObjFile.putClientProperty("border",entObjFile.getBorder());
					entObjFile.setBorder(BorderFactory.createLineBorder(Color.RED));
					entTextureFile.setBorder(BorderFactory.createLineBorder(Color.RED));
				}
				
			}
			
		});
		
		GroupLayout gl_entityCreator = new GroupLayout(entityCreator);
		gl_entityCreator.setHorizontalGroup(
			gl_entityCreator.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_entityCreator.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_entityCreator.createParallelGroup(Alignment.LEADING)
						.addComponent(createEntityBtn, GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)
						.addComponent(btnClose, Alignment.TRAILING)
						.addGroup(Alignment.TRAILING, gl_entityCreator.createSequentialGroup()
							.addGroup(gl_entityCreator.createParallelGroup(Alignment.TRAILING)
								.addGroup(Alignment.LEADING, gl_entityCreator.createSequentialGroup()
									.addGroup(gl_entityCreator.createParallelGroup(Alignment.LEADING, false)
										.addComponent(lblPositionX, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(lblPositionY, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(lblPositionZ, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_entityCreator.createParallelGroup(Alignment.LEADING)
										.addComponent(posXField, GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
										.addComponent(posYField, GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
										.addComponent(posZField, GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE))
									.addPreferredGap(ComponentPlacement.RELATED, 18, GroupLayout.PREFERRED_SIZE)
									.addGroup(gl_entityCreator.createParallelGroup(Alignment.LEADING)
										.addComponent(lblRotationY)
										.addComponent(lblRotationX, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(lblRotationZ)))
								.addGroup(Alignment.LEADING, gl_entityCreator.createSequentialGroup()
									.addGroup(gl_entityCreator.createParallelGroup(Alignment.LEADING)
										.addComponent(lblTexture)
										.addComponent(lblModel))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_entityCreator.createParallelGroup(Alignment.LEADING)
										.addComponent(entObjFile, GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
										.addComponent(entTextureFile, GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)))
								.addComponent(lblScale))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_entityCreator.createParallelGroup(Alignment.LEADING)
								.addGroup(Alignment.TRAILING, gl_entityCreator.createParallelGroup(Alignment.LEADING, false)
									.addComponent(browseTextures, GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
									.addComponent(browseObjFiles, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addComponent(scaleField, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
								.addComponent(rotZField, GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
								.addComponent(rotYField, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
								.addComponent(rotXField, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE))))
					.addContainerGap())
		);
		gl_entityCreator.setVerticalGroup(
			gl_entityCreator.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_entityCreator.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_entityCreator.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblModel)
						.addComponent(entObjFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(browseObjFiles))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_entityCreator.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTexture)
						.addComponent(entTextureFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(browseTextures))
					.addGap(18)
					.addGroup(gl_entityCreator.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_entityCreator.createSequentialGroup()
							.addGroup(gl_entityCreator.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblPositionX)
								.addComponent(posXField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblRotationX)
								.addComponent(rotXField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_entityCreator.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblPositionY)
								.addComponent(posYField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblRotationY)
								.addComponent(rotYField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_entityCreator.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblPositionZ)
								.addComponent(posZField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_entityCreator.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblRotationZ)
							.addComponent(rotZField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_entityCreator.createParallelGroup(Alignment.BASELINE)
						.addComponent(scaleField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblScale))
					.addGap(18)
					.addComponent(createEntityBtn)
					.addPreferredGap(ComponentPlacement.RELATED, 558, Short.MAX_VALUE)
					.addComponent(btnClose)
					.addContainerGap())
		);
		entityCreator.setLayout(gl_entityCreator);
		
		glCanvas.addMouseMotionListener(this);
		glCanvas.addMouseListener(this);
		glCanvas.addMouseWheelListener(this);
		
		pack();
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setForeground(Color.BLACK);
		menuBar.setBackground(Color.DARK_GRAY);
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		mnFile.setForeground(Color.WHITE);
		menuBar.add(mnFile);
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				
			}
			
		});
		mnFile.add(mntmOpen);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mnFile.add(mntmSave);
		
		JMenu mnEntity = new JMenu("Entities");
		mnEntity.setForeground(Color.WHITE);
		menuBar.add(mnEntity);
		
		JMenuItem mntmImport = new JMenuItem("Import");
		mnEntity.add(mntmImport);
		
		JMenuItem mntmCreate = new JMenuItem("Create");
		mntmCreate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				entityCreator.setVisible(true);
			}
			
		});
		mnEntity.add(mntmCreate);
		
		JMenu mnTerrain = new JMenu("Terrains");
		mnTerrain.setForeground(Color.WHITE);
		menuBar.add(mnTerrain);
		
		JMenuItem mntmImport_1 = new JMenuItem("Import");
		mnTerrain.add(mntmImport_1);
		
		JMenu mnLights = new JMenu("Lights");
		mnLights.setForeground(Color.WHITE);
		menuBar.add(mnLights);
		
		JMenuItem mntmImport_2 = new JMenuItem("Import");
		mnLights.add(mntmImport_2);
		this.setVisible(true);
		this.transferFocus();
		lastTimeInterval = System.currentTimeMillis();
		
		Runnable renderLoop = new Runnable() {

			@Override
			public void run() {
				if(!glCanvas.isValid())
				{
					return;
				}
				glCanvas.render();
				SwingUtilities.invokeLater(this);
				
			}
			
		};
		SwingUtilities.invokeLater(renderLoop);
		
		
	}
	
	public static float getFrameTime()
	{
		return deltaTime;
	}
	
	public static float getMouseDX()
	{
		return mouseDX;
	}
	
	public static float getMouseDY()
	{
		return mouseDY;
	}
	
	public static void update()
	{
		long currTime = System.currentTimeMillis();
		deltaTime = (float) ((currTime - lastTimeInterval)/1000f);
		lastTimeInterval = currTime;
	}
	
	public static boolean isMouseButtonDown(int button)
	{
		return mouseButtons[button];
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		mouseDX = event.getX() - lastMouseX;
		mouseDY = event.getY() - lastMouseY;
		lastMouseX = event.getX();
		lastMouseY = event.getY();
		if(isMouseButtonDown(MouseEvent.BUTTON1))
		{
			float orbit = mouseDX * 0.3f;
			camera.calcOrbit(orbit);
		}
		
		if(isMouseButtonDown(MouseEvent.BUTTON3))
		{
			float pitch = mouseDY * 0.1f;
			camera.calcPitch(pitch);
		}
		
		
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		
	}

	@Override
	public void mouseEntered(MouseEvent event) {
		
	}

	@Override
	public void mouseExited(MouseEvent event) {
		
	}

	@Override
	public void mousePressed(MouseEvent event) {
		lastMouseX = event.getX();
		lastMouseY = event.getY();
		mouseDX = 0;
		mouseDY = 0;
		
		// Set mouse button pressed
		mouseButtons[event.getButton()] = true;
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		// Set mouse button released
		mouseButtons[event.getButton()] = false;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		camera.calcZoom(event.getWheelRotation());
	}
}
