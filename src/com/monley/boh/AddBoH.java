package com.monley.boh;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.html.HTMLTableSectionElement;

import com.google.gson.Gson;
import com.monley.db.Daodb;
import com.monley.db.Tab_tspserver;
import com.monley.db.Tab_tspserver_boh_relation;
import com.monley.result.ResultSummary;

import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;


/**
 * Servlet implementation class AddBoH
 */
@WebServlet("/addboh")
public class AddBoH extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddBoH() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		PrintWriter out = response.getWriter();
		int key=-1; //define boh var,store the id
		int tspCount=-1; //define tsp var,store sum tsp count
		ArrayList<Tab_tspserver_boh_relation> tspFromPost = new ArrayList<>(); //get tsp paramter
		ResultSummary returnRes = new ResultSummary();
		//get post boh data
		String name = request.getParameter("name").trim();
		String bohName = request.getParameter("bohName").trim();
		String bohMethod = request.getParameter("bohMethod").trim();
		String bohRoutePath = request.getParameter("bohRoutePath").trim();
		String bohParameter = request.getParameter("bohParameter");
		String sampleTxt = request.getParameter("sampleTxt");
		
		if(name.length()==0||bohName.length()==0||bohMethod.length()==0||bohRoutePath.length()==0){
			Gson gson = new Gson();
		    returnRes=new ResultSummary();
			returnRes.setSuccess(false);
			returnRes.setInfo("Paramter can not be empty");
			String strJson=gson.toJson(returnRes);
			out.print(strJson);
			return;
		}
		//String tsp_1 = request.getParameter("tsp_1");
		//String tsp_2 = request.getParameter("tsp_2");
		//String tsp_3 = request.getParameter("tsp_3");
		
		Daodb  db = new Daodb("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/mobcenter", "root", "security");
		try {
			 key = db.insert("insert into mobcenter.boh(Name,BohName,BohMethod,BohRoutePath,BohParameter,SampleTxt)  values (?,?,?,?,?,?)",
															name,bohName,bohMethod,bohRoutePath,bohParameter,sampleTxt);
			System.out.printf("key is %s",key);
			System.out.println();
			
			//get tsp information
			ArrayList<Tab_tspserver> tspList=new ArrayList<>();
			ResultSet rs = db.query("select ID,Name from mobcenter.tsp_server");
			
			while(rs.next()){
				Tab_tspserver tspTmp =new Tab_tspserver();
				tspTmp.setId(rs.getInt("ID"));
				tspTmp.setName(rs.getString("Name"));
				tspList.add(tspTmp);
			}
			//get tsp Parameter
			for(int i=0;i<tspList.size();i++){
				Tab_tspserver_boh_relation tsp=new Tab_tspserver_boh_relation();
				String tspTmp="tsp_"+tspList.get(i).getId();//construct tsp var
				tspTmp=request.getParameter(tspTmp);
				tsp.setTsp_server_ID(tspList.get(i).getId());
				tsp.setServiceHost(tspTmp);
				tsp.setBoh_ID(key);
				tspFromPost.add(tsp);
			}
			
			for (Tab_tspserver_boh_relation tsp : tspFromPost) {
				System.out.printf("the tsp_boh_relation data is %s", "Boh key: "+ tsp.getBoh_ID() +" Tsp key: " +tsp.getTsp_server_ID()+" Host : "+tsp.getServiceHost());
				System.out.println();
			}
			
			//insert boh_tsp_relation to db
			for (Tab_tspserver_boh_relation tsp : tspFromPost) {
				db.insert("insert into mobcenter.tspserver_boh_relation(Boh_ID,Tsp_server_ID,ServiceHost) values (?,?,?)", 
																		tsp.getBoh_ID(),tsp.getTsp_server_ID(),tsp.getServiceHost());
			}
			returnRes.setSuccess(true);
			returnRes.setInfo("success insert boh data");
			Gson gson = new Gson();
			String strJson=gson.toJson(returnRes);
			out.print(strJson);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			returnRes.setSuccess(false);
			returnRes.setInfo("insert boh data fail.");
			Gson gson = new Gson();
			String strJson=gson.toJson(returnRes);
			out.print(strJson);
		}
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}