package java2ddrawingapplication;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;


public class DrawingApplicationFrame extends JFrame
{   

    // Create the panels for the top of the application. One panel for each
    // line and one to contain both of those panels.
    JPanel shapePanel = new JPanel(new FlowLayout());
    JPanel optionsPanel = new JPanel(new FlowLayout());
    JPanel mainPanel = new JPanel(new BorderLayout());
    DrawPanel drawPanel = new DrawPanel();

    // create the widgets for the firstLine Panel.
    JLabel shapeLabel = new JLabel("Shape:");
    JComboBox shapeBox = new JComboBox<>(new String[] { "Line", "Rectangle", "Oval" });
    JButton color1 = new JButton("1st Color");
    JButton color2 = new JButton("2nd Color");
    JButton undoButton = new JButton("Undo");
    JButton clearButton = new JButton("Clear");
    //create the widgets for the secondLine Panel.
    JLabel optionsLabel = new JLabel("Options:");
    JCheckBox filledCheck = new JCheckBox("Filled");
    JCheckBox gradientCheck = new JCheckBox("Use Gradient");
    JCheckBox dashedCheck = new JCheckBox("Dashed");
    SpinnerNumberModel widthModel = new SpinnerNumberModel(5, 1, 50, 1);
    SpinnerNumberModel lengthModel = new SpinnerNumberModel(5, 1, 50, 1);
    JLabel lineWidthLabel = new JLabel("Line Width:");
    JSpinner lineWidth = new JSpinner(widthModel);
    JLabel dashLengthLabel = new JLabel("Dash Length:");
    JSpinner dashLength = new JSpinner(lengthModel);
    // Variables for drawPanel.
    static ArrayList<MyShapes> shapes;
    Stroke stroke;
    // add status label
    JLabel status;
    // Constructor for DrawingApplicationFrame
    
    public DrawingApplicationFrame()
    {
        setTitle("Java 2D Drawings");
        // add widgets to panels
        // firstLine widgets
        status = new JLabel("( , )");
        shapePanel.add(shapeLabel);
        shapePanel.add(shapeBox);
        shapePanel.add(color1);
        shapePanel.add(color2);
        shapePanel.add(undoButton);
        shapePanel.add(clearButton);
        color1.setBackground(Color.BLACK);
        // secondLine widgets
        optionsPanel.add(optionsLabel);
        optionsPanel.add(filledCheck);
        optionsPanel.add(gradientCheck);
        optionsPanel.add(dashedCheck);
        optionsPanel.add(lineWidthLabel);
        optionsPanel.add(lineWidth);
        optionsPanel.add(dashLengthLabel);
        optionsPanel.add(dashLength);
        // add top panel of two panels
        mainPanel.add(shapePanel, BorderLayout.NORTH);
        mainPanel.add(optionsPanel, BorderLayout.SOUTH);
        shapePanel.setBackground(Color.CYAN);
        optionsPanel.setBackground(Color.CYAN);
        // add topPanel to North, drawPanel to Center, and statusLabel to South
        add(mainPanel, BorderLayout.NORTH);
        add(status, BorderLayout.SOUTH);
        //add(drawPanel, BorderLayout.CENTER);
        add(drawPanel, BorderLayout.CENTER);
        //add listeners and event handlers
        
        undoButton.addActionListener((ActionEvent ae) -> {
            if (!shapes.isEmpty()) {
                shapes.remove(shapes.size()-1);
                repaint();
            }
        });
        clearButton.addActionListener((ActionEvent ae) -> {
            if (clearButton == ae.getSource()) {
                shapes.clear();
                repaint();
            }
        });
        color1.addActionListener((ActionEvent event) -> {
            color1.setBackground(JColorChooser.showDialog(null, "Pick your color", Color.BLACK));
        });
        color2.addActionListener((ActionEvent event) -> {
            color2.setBackground(JColorChooser.showDialog(null, "Pick your color", Color.WHITE));
        });
    }



    private class DrawPanel extends JPanel
    {
        Paint paint;
        public DrawPanel()
        {
            MouseHandler mouseHandler = new MouseHandler();
            addMouseMotionListener(mouseHandler);
            addMouseListener(mouseHandler);
            shapes = new ArrayList<>();
        }
        
        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            shapes.forEach(d -> {
                d.draw(g2d);
            });
}
        public void addComponent(MyShapes shape) {
            shapes.add(shape);
            repaint();
        }
        public MyShapes createShape(Point start, Point finish, Paint paint, Stroke strk, Boolean isFilled) {
                MyShapes shape = null;
		String selected = (String) shapeBox.getSelectedItem();
		if (null != selected) switch (selected) {
                case "Rectangle":
                    shape = new MyRectangle(start, finish, paint, strk, isFilled);
                    break;
                case "Oval":
                    shape = new MyOval(start, finish, paint, strk, isFilled);
                    break;
                case "Line":
                    shape = new MyLine(start, finish, paint, strk);
                    break;
                default:
                    break;
            }
		return shape;
	}


        private class MouseHandler extends MouseAdapter implements MouseMotionListener
        {
            private Point clickPoint;
            private MyShapes shape;
            

            @Override
            public void mousePressed(MouseEvent event)
            {      
                clickPoint = event.getPoint();
                if (gradientCheck.isSelected()) {
                    paint = new GradientPaint(0, 0, color1.getBackground(), 50, 50, color2.getBackground(), true);
                } else {
                    paint = color1.getBackground();
                }
                 if (dashedCheck.isSelected())
                {
                    stroke = new BasicStroke(widthModel.getNumber().floatValue(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, new float[] {lengthModel.getNumber().floatValue()}, 0);
                } else {
                    stroke = new BasicStroke(widthModel.getNumber().floatValue(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            }
                 shape = createShape(clickPoint, clickPoint, paint, stroke, filledCheck.isSelected());
                 drawPanel.addComponent(shape);
		 
            }


            @Override
            public void mouseReleased(MouseEvent event)
            {
                drawPanel.repaint();
            }


            @Override
            public void mouseDragged(MouseEvent event)
            {
                Point drag = event.getPoint();
		Point start = clickPoint;
		shape.setStartPoint(start);
		shape.setEndPoint(drag);
                String position = "(" + event.getX() + "," + event.getY() + ")";
                status.setText(position);
                drawPanel.repaint();
            }

            @Override
            public void mouseMoved(MouseEvent event)
            {
                String position = "(" + event.getX() + "," + event.getY() + ")";
                status.setText(position);
            }
        }

    }
}
