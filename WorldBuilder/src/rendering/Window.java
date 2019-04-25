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
import java.util.Iterator;
import java.util.List;

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
	
	private List<String> loadFileQueue;
	
	private List<Terrain> terrains = new ArrayList<>();
	private List<Light> lights = new ArrayList<>();
	private List<Entity> entities = new ArrayList<>();
	private List<AnimatedCharacter> animChars = new ArrayList<>();
	
	private AdvancedRenderer renderer;
	
	private Camera camera;
	
	private Loader loader;
	private JTextField entObjFile;
	
	public Window(Loader loader)
	{
		this.loader = loader;
		camera = new Camera();
		loadFileQueue = new ArrayList<>();
		init();
	}
	
	private void init()
	{
		setTitle("World Builder");
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
				Iterator<String> it = loadFileQueue.iterator();
				while(it.hasNext())
				{
					String filePath = it.next();
					BaseModel test = OBJLoader.loadObj(filePath, loader);
					it.remove();
				}
				camera.move();
				renderer.renderScene(entities, terrains, animChars, lights, camera, new Vector4f(0,-1f,0,0));
				
				
				Window.update();
				swapBuffers();
			}
			
		}, BorderLayout.CENTER);
		
		JPanel entityPanel = new JPanel();
		Border entityPanelBorder = BorderFactory.createTitledBorder("Entity Editor");
		((TitledBorder) entityPanelBorder).setTitleJustification(TitledBorder.CENTER);
		getContentPane().add(entityPanel, BorderLayout.EAST);
		entityPanel.setBorder(entityPanelBorder);
		entityPanel.setVisible(false);
		
		JLabel lblModel = new JLabel("Model:");
		
		entObjFile = new JTextField();
		entObjFile.setColumns(10);
		
		JButton browseObjFiles = new JButton("Browse");
		browseObjFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				entityPanel.setVisible(false);
				
			}
			
		});
		GroupLayout gl_entityPanel = new GroupLayout(entityPanel);
		gl_entityPanel.setHorizontalGroup(
			gl_entityPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_entityPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_entityPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_entityPanel.createSequentialGroup()
							.addComponent(lblModel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(entObjFile, GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(browseObjFiles))
						.addGroup(Alignment.TRAILING, gl_entityPanel.createSequentialGroup()
							.addComponent(btnClose)
							.addContainerGap())))
		);
		gl_entityPanel.setVerticalGroup(
			gl_entityPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_entityPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_entityPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblModel)
						.addComponent(entObjFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(browseObjFiles))
					.addPreferredGap(ComponentPlacement.RELATED, 749, Short.MAX_VALUE)
					.addComponent(btnClose)
					.addContainerGap())
		);
		entityPanel.setLayout(gl_entityPanel);
		
		glCanvas.addMouseMotionListener(this);
		glCanvas.addMouseListener(this);
		glCanvas.addMouseWheelListener(this);
		
		pack();
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
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
		menuBar.add(mnEntity);
		
		JMenuItem mntmImport = new JMenuItem("Import");
		mnEntity.add(mntmImport);
		
		JMenuItem mntmCreate = new JMenuItem("Create");
		mntmCreate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				entityPanel.setVisible(true);
			}
			
		});
		mnEntity.add(mntmCreate);
		
		JMenu mnTerrain = new JMenu("Terrains");
		menuBar.add(mnTerrain);
		
		JMenuItem mntmImport_1 = new JMenuItem("Import");
		mnTerrain.add(mntmImport_1);
		
		JMenu mnLights = new JMenu("Lights");
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
