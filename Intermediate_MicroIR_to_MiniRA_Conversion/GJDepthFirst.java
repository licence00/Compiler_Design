//
// Generated by JTB 1.3.2
//

package visitor;
import syntaxtree.*;
import java.util.*;

/**
 * Provides default methods which visit each node in the tree in depth-first
 * order.  Your visitors may extend this class.
 */
public class GJDepthFirst<R,A> implements GJVisitor<R,A> {
   //
   // Auto class visitors--probably don't need to be overridden.
   //
   boolean debug = false;
   int p=0;
   int stmt_count = 0;
   HashMap<String,Integer> local_label = new HashMap<String,Integer>();

   HashMap<Integer,Set<Integer>> use = new HashMap<Integer,Set<Integer>>();
   HashMap<Integer,Set<Integer>> def = new HashMap<Integer,Set<Integer>>();
   
   HashMap<Integer,Set<Integer>> succ = new HashMap<Integer,Set<Integer>>();
   HashMap<String,Integer> proc_args = new HashMap<String,Integer>(); //args,stack,max_no_of_args
   HashMap<String,Integer> proc_stack = new HashMap<String,Integer>(); //args,stack,max_no_of_args
   HashMap<String,Integer> proc_max__args = new HashMap<String,Integer>(); //args,stack,max_no_of_args
   HashMap<String,Integer> calls = new HashMap<String,Integer>();
   HashMap<String,HashMap<Integer,Allocated>> register_allocation = new HashMap<String,HashMap<Integer,Allocated>>();
   HashMap<String,HashMap<String,Integer>> label_number = new HashMap<String,HashMap<String,Integer>>();

   public class Allocated
   {
      String register;
      boolean spilled;
      int spilled_loc;
      Allocated()
      {
         register = "";
         spilled = false;
         spilled_loc = -1;
      }
      public void print()
      {
         System.out.println("register is : "+register);
         System.out.println("spilled is : "+spilled);
         System.out.println("spilled_loc is : "+spilled_loc);
      }
   }

   public class live_intervals_Comparator implements Comparator<Vector<Integer>> 
   {
      public int compare(Vector<Integer>s1, Vector<Integer>s2) 
      {
         if(s1.get(0) < s2.get(0))
         {
            return -1;
         }
         else if(s1.get(0) == s2.get(0))
         {
            if(s1.get(1) < s2.get(1))
            {
               return -1;
            }
            else
            {
               return 1;
            }
         }
         else
         {
            return 1;
         }
      }
   }

   public class end_Comparator implements Comparator<Vector<Integer>> 
   {
      public int compare(Vector<Integer>s1, Vector<Integer>s2) 
      {
         if(s1.get(1) < s2.get(1)) { return -1; }
         else if(s1.get(1) == s2.get(1))
         {
            if(s1.get(0) < s2.get(0)) { return -1; }
            else { return 1; }
         }
         else { return 1;}
      }
   }

   public void linear_Scan_Algorithm(Vector<Vector<Integer>>intervals,String method_name)
   {
      List<Vector<Integer>> active_temps = new ArrayList<Vector<Integer>>();
      List<String> free_registers = new ArrayList<String>();
      for(int i=0;i<=7;i++)
      {
         free_registers.add("s"+i);
      }
      for(int i=0;i<=9;i++)
      {
         free_registers.add("t"+i);
      }
      int R = 18;
      for(int i=0;i<intervals.size();i++)
      {
         Vector<String>expire_registers = expireOldIntervals(intervals.get(i),active_temps,method_name);
         // System.out.println("expire registers are : "+expire_registers);
         //free_registers.addAll(expire_registers);
         for(int k=0;k<expire_registers.size();k++)
         {
            free_registers.add(0,expire_registers.get(k));
         }
         // System.out.println("free registers are : "+free_registers+ " and the size is : "+free_registers.size());

         if(active_temps.size()==R)
         {
            spillAtInterval(intervals.get(i),active_temps,method_name);
         }
         else
         {
            String register = free_registers.get(0); free_registers.remove(0);
            Allocated temp = new Allocated();
            temp.register = register;
            register_allocation.get(method_name).put(intervals.get(i).get(2),temp);
            active_temps.add(intervals.get(i));
            Collections.sort(active_temps,new end_Comparator());
            System.out.println("register allocated is : "+register+" for temp : "+intervals.get(i).get(2)+ " for method : "+ method_name);
            System.out.println("live interval are : " + intervals.get(i).get(0) + " " + intervals.get(i).get(1));
         }
      }
   }

   public Vector<String> expireOldIntervals(Vector<Integer>interval,List<Vector<Integer>>active_temps,String method_name)
   {
      Vector<String>temp = new Vector<String>();
      for(int j=0;j<active_temps.size();j++)
      {
         if(active_temps.get(j).get(1) >= interval.get(0))
         {
            continue;
         }
         else
         {
            String free_reg = register_allocation.get(method_name).get(active_temps.get(j).get(2)).register;
            temp.add(free_reg);
            active_temps.remove(j);
         }
      }
      return temp;
   }

   public void spillAtInterval(Vector<Integer>interval,List<Vector<Integer>>active_temps,String method_name)
   {
      Vector<Integer>spill_interval = active_temps.get(active_temps.size()-1);
      if(spill_interval.get(1) > interval.get(1))
      {
         Allocated reg = register_allocation.get(method_name).get(spill_interval.get(2));
         register_allocation.get(method_name).put(interval.get(2),reg);
         System.out.println("register allocated is : "+reg.register+" for temp : "+interval.get(2)+ " for method : "+ method_name);
         register_allocation.get(method_name).remove(spill_interval.get(2));
         Allocated temp = new Allocated();
         temp.spilled = true; 
         int stack_pos = proc_stack.get(method_name); temp.spilled_loc = stack_pos;
         proc_stack.put(method_name,stack_pos+1);
         register_allocation.get(method_name).put(spill_interval.get(2),temp);
         active_temps.remove(active_temps.size()-1);
         active_temps.add(interval);
         Collections.sort(active_temps,new end_Comparator());
      }
      else
      {
         Allocated temp = new Allocated();
         temp.spilled = true; 
         int stack_pos = proc_stack.get(method_name); temp.spilled_loc = stack_pos;
         proc_stack.put(method_name,stack_pos+1);
         register_allocation.get(method_name).put(interval.get(2),temp);
      }
   }


   public R visit(NodeList n, A argu) {
      R _ret=null;
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this,argu);
         _count++;
      }
      return _ret;
   }

   public R visit(NodeListOptional n, A argu) {
      if ( n.present() ) {
         R _ret=null;
         int _count=0;
         List<R> arguments = new ArrayList<R>();
         for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
           R temp = (R)e.nextElement().accept(this,argu);
           arguments.add(temp); 
            _count++;
         }
         _ret = (R)arguments;
         return _ret;
      }
      else
         return null;
   }

   public R visit(NodeOptional n, A argu) {
      if ( n.present() )
      {
         R _ret = n.node.accept(this,argu);
         if(p==0)
         {
            // if(debug)
            // {
            //    System.out.println("the statemetn count is : "+stmt_count);
            // }
            local_label.put((String)_ret,stmt_count+1);
         }
         //System.out.println("node optional is present : "+(String)_ret);
         return _ret;
      }
      else
      {
         return null;
      }
   }

   public R visit(NodeSequence n, A argu) {
      R _ret=null;
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this,argu);
         _count++;
      }
      return _ret;
   }

   public R visit(NodeToken n, A argu) { return (R)n.tokenImage; }

   //
   // User-generated visitor methods below
   //

   /**
    * f0 -> "MAIN"
    * f1 -> StmtList()
    * f2 -> "END"
    * f3 -> ( Procedure() )*
    * f4 -> <EOF>
    */
   public R visit(Goal n, A argu) {
      R _ret=null;
     
      stmt_count = 0;
      String method_name = "MAIN";
      local_label = new HashMap<String,Integer>();
      label_number.put("MAIN",local_label);
      HashMap<Integer,Allocated> new_allocation = new HashMap<Integer,Allocated>();
      register_allocation.put("MAIN",new_allocation);
      proc_args.put("MAIN",0); proc_stack.put("MAIN",0); proc_max__args.put("MAIN",-1); calls.put("MAIN",0);

      n.f0.accept(this, argu);
      n.f1.accept(this, (A)method_name);
      n.f2.accept(this, argu);
      n.f3.accept(this, (A)method_name);
      n.f4.accept(this, argu);

      //System.out.println(label_number);
      
      p++;
      stmt_count = 0;
      n.f0.accept(this, argu);
      n.f1.accept(this, (A)method_name);
      n.f2.accept(this, argu);

      HashMap<Integer,Set<Integer>> in = new HashMap<Integer,Set<Integer>>();
      HashMap<Integer,Set<Integer>> out = new HashMap<Integer,Set<Integer>>();
      for(int i=1;i<=stmt_count;i++)
      {
         in.put(i,new HashSet<Integer>());
         out.put(i,new HashSet<Integer>());
      }

      succ.get(stmt_count).add(-100); //doesn't have an successor after end
      succ.get(stmt_count).remove(stmt_count+1); //remove the successor of the last statement

      // System.out.println(use);
      // System.out.println(def);
      // System.out.println(succ);
      while(true)
      {
         boolean change = true;
         for(int i=1;i<=stmt_count;i++)
         {
            int initial_in_size = in.get(i).size();
            int initial_out_size = out.get(i).size();
            // System.out.print("stmt is : "+i);
            for(int j:succ.get(i))
            {
               if(j!=-100) //end statement no successor
               {
                  out.get(i).addAll(in.get(j));
               }
            }
            // System.out.print(" out is : "+out.get(i));
            int final_out_size = out.get(i).size();

            Set<Integer> temp = new HashSet<Integer>();
            temp.addAll(out.get(i));
            temp.removeAll(def.get(i));
            for(int j:use.get(i))
            {
               temp.add(j);
            }
            in.put(i,temp);
            // System.out.println(" in is : "+in.get(i));
            int final_in_size = temp.size();
             
            change = change && initial_in_size==final_in_size && initial_out_size==final_out_size;
         }
         if(change)
         {
            break;
         }
      }

      // System.out.println("in is : \n");
      // System.out.println(in);
      // System.out.println("\n");

      // System.out.println("out is : \n");
      // System.out.println(out);
      // System.out.println("\n");

      Set<Integer> temp_list = new HashSet<Integer>();
      for(int i=1;i<=stmt_count;i++)
      {
         temp_list.addAll(use.get(i));
         temp_list.addAll(def.get(i));
      }

      HashMap<Integer,Vector<Integer>> temp_live_intervals = new HashMap<Integer,Vector<Integer>>();
      Set<Integer> temp_set = new HashSet<Integer>();
      for(int i=1;i<=stmt_count;i++)
      {
         temp_set.addAll(in.get(i));
         temp_set.addAll(out.get(i));
      }
      for(int j : temp_set)
      {
         temp_live_intervals.put(j,new Vector<Integer>());
         temp_live_intervals.get(j).add(-1); temp_live_intervals.get(j).add(-1); 
      }

      for(int i=1;i<=stmt_count;i++)
      {
         for(int j : out.get(i))
         {
            if(temp_live_intervals.get(j).get(0)==-1)
            {
               int second_value = temp_live_intervals.get(j).get(1);
               temp_live_intervals.get(j).clear();
               temp_live_intervals.get(j).add(i); temp_live_intervals.get(j).add(second_value);
            }
         }
         for(int j : in.get(i))
         {
            if(temp_live_intervals.get(j).get(1) < i)
            {
               int first_value = temp_live_intervals.get(j).get(0);
               temp_live_intervals.get(j).clear();
               temp_live_intervals.get(j).add(first_value); temp_live_intervals.get(j).add(i);
            }
         }   
      }

      Vector<Vector<Integer>> sorted_live_intervals = new Vector<Vector<Integer>>();
      for (Map.Entry<Integer,Vector<Integer>> entry : temp_live_intervals.entrySet()) 
      {
         Vector<Integer> temp = new Vector<Integer>();
         temp.add(entry.getValue().get(0)); temp.add(entry.getValue().get(1)); temp.add(entry.getKey());
         sorted_live_intervals.add(temp);
      }

      Collections.sort(sorted_live_intervals,new live_intervals_Comparator());

      // System.out.println("end live intervals are : \n");
      // System.out.println(sorted_live_intervals);
      // System.out.println("\n");
      
      linear_Scan_Algorithm(sorted_live_intervals,method_name);


      // for(Integer key : register_allocation.get(method_name).keySet())
      // {
      //    System.out.print("key is : "+key.toString()+" ");
      //    Allocated temp = register_allocation.get(method_name).get(key);
      //    System.out.println("register is " + temp.register);
      // }

      if(calls.get(method_name)>0)
      {
         int stack_pos = proc_stack.get(method_name);
         proc_stack.put(method_name,stack_pos+10);
      }

      n.f3.accept(this, (A)method_name);
      n.f4.accept(this, argu);

      // System.out.println("use is : \n");
      // System.out.println(use);
      // System.out.println("\n");

      // System.out.println("def is : \n");
      // System.out.println(def);
      // System.out.println("\n");

      // System.out.println("succ is : \n");
      // System.out.println(succ);
      // System.out.println("\n");


      return _ret;
   }

   /**
    * f0 -> ( ( Label() )? Stmt() )*
    */
   public R visit(StmtList n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> Label()
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> StmtExp()
    */
   public R visit(Procedure n, A argu) {
      R _ret=null;
      if(p==0)
      {
         stmt_count++;
         String proc_name = (String)n.f0.accept(this, argu);
         // if(debug)
         // {
         //    System.out.println("proc name is : "+proc_name);
         // }
         n.f1.accept(this, argu);
         local_label = new HashMap<String,Integer>();
         label_number.put(proc_name,local_label);
         int number_of_args = Integer.parseInt((String)n.f2.accept(this, argu));
         // if(debug)
         // {
         //    System.out.println("number of args is : "+number_of_args);
         // }
         // Vector<Integer> temp = new Vector<Integer>();
         // temp.add(number_of_args); temp.add(0); temp.add(0);
         // label.put(proc_name,temp);
         n.f3.accept(this, argu);
         
         n.f4.accept(this, argu);
      }
      else if(p==1)
      {
         int start_stmt = stmt_count; stmt_count++;
         String method_name = (String)n.f0.accept(this, argu);

         HashMap<Integer,Allocated> new_allocation = new HashMap<Integer,Allocated>();
         register_allocation.put(method_name,new_allocation);
         proc_stack.put(method_name,0); proc_max__args.put(method_name,-1); calls.put(method_name,0);

         use.put(stmt_count,new HashSet<Integer>());
         def.put(stmt_count,new HashSet<Integer>());
         succ.put(stmt_count,new HashSet<Integer>());
         succ.get(stmt_count).add(stmt_count+1);
         n.f1.accept(this, argu);
         int number_of_args = Integer.parseInt((String)n.f2.accept(this, argu));
         proc_args.put(method_name,number_of_args);

         for(int i=0;i<number_of_args;i++)
         {
            def.get(stmt_count).add(i);
         }
         n.f3.accept(this, argu);
         n.f4.accept(this, (A)method_name);

         HashMap<Integer,Set<Integer>> in = new HashMap<Integer,Set<Integer>>();
         HashMap<Integer,Set<Integer>> out = new HashMap<Integer,Set<Integer>>();
         for(int i=start_stmt;i<=stmt_count;i++)
         {
            in.put(i,new HashSet<Integer>());
            out.put(i,new HashSet<Integer>());
         }

         while(true)
         {
            boolean change = true;
            for(int i=start_stmt;i<=stmt_count;i++)
            {
               int initial_in_size = in.get(i).size();
               int initial_out_size = out.get(i).size();
               // System.out.print("stmt is : "+i);
               for(int j:succ.get(i))
               {
                  if(j!=-100) //end statement no successor
                  {
                     out.get(i).addAll(in.get(j));
                  }
               }
               // System.out.print(" out is : "+out.get(i));
               int final_out_size = out.get(i).size();

               Set<Integer> temp = new HashSet<Integer>();
               temp.addAll(out.get(i));
               temp.removeAll(def.get(i));
               for(int j:use.get(i))
               {
                  temp.add(j);
               }
               in.put(i,temp);
               // System.out.println(" in is : "+in.get(i));
               int final_in_size = temp.size();
               
               change = change && initial_in_size==final_in_size && initial_out_size==final_out_size;
            }
            if(change)
            {
               break;
            }
         }

         // System.out.println("in is : \n");
         // System.out.println(in);
         // System.out.println("\n");

         // System.out.println("out is : \n");
         // System.out.println(out);
         // System.out.println("\n");

         Set<Integer> temp_list = new HashSet<Integer>();
         for(int i=start_stmt;i<=stmt_count;i++)
         {
            temp_list.addAll(use.get(i));
            temp_list.addAll(def.get(i));
         }

         // System.out.println("temp registers in " + method_name);
         // System.out.println(temp_list);

         HashMap<Integer,Vector<Integer>> temp_live_intervals = new HashMap<Integer,Vector<Integer>>();
         Set<Integer> temp_set = new HashSet<Integer>();
         for(int i=start_stmt;i<=stmt_count;i++)
         {
            temp_set.addAll(in.get(i));
            temp_set.addAll(out.get(i));
         }
         for(int j : temp_set)
         {
            temp_live_intervals.put(j,new Vector<Integer>());
            temp_live_intervals.get(j).add(-1); temp_live_intervals.get(j).add(-1); 
         }

         for(int i=start_stmt;i<=stmt_count;i++)
         {
            for(int j : out.get(i))
            {
               if(temp_live_intervals.get(j).get(0)==-1)
               {
                  int second_value = temp_live_intervals.get(j).get(1);
                  temp_live_intervals.get(j).clear();
                  temp_live_intervals.get(j).add(i); temp_live_intervals.get(j).add(second_value);
               }
            }
            for(int j : in.get(i))
            {
               if(temp_live_intervals.get(j).get(1) < i)
               {
                  int first_value = temp_live_intervals.get(j).get(0);
                  temp_live_intervals.get(j).clear();
                  temp_live_intervals.get(j).add(first_value); temp_live_intervals.get(j).add(i);
               }
            }   
         }

         // System.out.println("live intervals are : \n");
         // System.out.println(temp_live_intervals);
         // System.out.println("\n");

         Vector<Vector<Integer>> sorted_live_intervals = new Vector<Vector<Integer>>();
         for (Map.Entry<Integer,Vector<Integer>> entry : temp_live_intervals.entrySet()) 
         {
            Vector<Integer> temp = new Vector<Integer>();
            temp.add(entry.getValue().get(0)); temp.add(entry.getValue().get(1)); temp.add(entry.getKey());
            sorted_live_intervals.add(temp);
         }

         Collections.sort(sorted_live_intervals,new live_intervals_Comparator());
         
         // System.out.println("sorted live intervals are : \n");
         // System.out.println(sorted_live_intervals);
         // System.out.println("\n");


         // System.out.println("end live intervals are : \n");
         // System.out.println(sorted_live_intervals);
         // System.out.println("\n");
         
         linear_Scan_Algorithm(sorted_live_intervals,method_name);

         int stack_pos = proc_stack.get(method_name);
         proc_stack.put(method_name,stack_pos+8);

         if(calls.get(method_name)>0)
         {
            stack_pos = proc_stack.get(method_name);
            proc_stack.put(method_name,stack_pos+10);
         }

         // System.out.println(register_allocation.get(method_name));


      }
      return _ret;
   }

   /**
    * f0 -> NoOpStmt()
    *       | ErrorStmt()
    *       | CJumpStmt()
    *       | JumpStmt()
    *       | HStoreStmt()
    *       | HLoadStmt()
    *       | MoveStmt()
    *       | PrintStmt()
    */
   public R visit(Stmt n, A argu) {
      R _ret=null;
      if(p==0)
      {
         stmt_count++;
         n.f0.accept(this, argu);
      }
      else if(p==1)
      {
         stmt_count++;
         n.f0.accept(this, argu);
      }
      else if(p==2)
      {
         
      }
      //n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "NOOP"
    */
   public R visit(NoOpStmt n, A argu) {
      R _ret=null;
      if(p==0)
      {
         n.f0.accept(this, argu);
      }
      else if (p==1)
      {
         use.put(stmt_count,new HashSet<Integer>());
         def.put(stmt_count,new HashSet<Integer>());
         succ.put(stmt_count,new HashSet<Integer>());
         succ.get(stmt_count).add(stmt_count+1);
         n.f0.accept(this, argu);
      }
      else if(p==2)
      {
         System.out.println("NOOP");
         n.f0.accept(this, argu);
      }
      
      return _ret;
   }

   /**
    * f0 -> "ERROR"
    */
   public R visit(ErrorStmt n, A argu) {
      R _ret=null;
      if(p==0)
      {
         n.f0.accept(this, argu);
      }
      else if(p==1)
      {
         use.put(stmt_count,new HashSet<Integer>());
         def.put(stmt_count,new HashSet<Integer>());
         succ.put(stmt_count,new HashSet<Integer>());
         succ.get(stmt_count).add(stmt_count+1);
         n.f0.accept(this, argu);
      }
      else if(p==2)
      {
         System.out.println("ERROR");
         n.f0.accept(this, argu);
      }
      return _ret;
   }

   /**
    * f0 -> "CJUMP"
    * f1 -> Temp()
    * f2 -> Label()
    */
   public R visit(CJumpStmt n, A argu) {
      R _ret=null;
      if(p==0)
      {
         n.f0.accept(this, argu);
         n.f1.accept(this, argu);
         n.f2.accept(this, argu);
      }
      else if(p==1)
      {
         use.put(stmt_count,new HashSet<Integer>());
         def.put(stmt_count,new HashSet<Integer>());
         succ.put(stmt_count,new HashSet<Integer>());
         succ.get(stmt_count).add(stmt_count+1);
         String method_name = (String)argu;
         String temp_index = (String)n.f1.accept(this, argu);
         String label_name = (String)n.f2.accept(this, argu);
         succ.get(stmt_count).add(label_number.get(method_name).get(label_name));
         use.get(stmt_count).add(Integer.parseInt(temp_index));
      }
      else if(p==2)
      {

      }
      return _ret;
   }

   /**
    * f0 -> "JUMP"
    * f1 -> Label()
    */
   public R visit(JumpStmt n, A argu) {
      R _ret=null;
      if(p==0)
      {
         n.f0.accept(this, argu);
         n.f1.accept(this, argu);
      }
      else if(p==1)
      {
         use.put(stmt_count,new HashSet<Integer>());
         def.put(stmt_count,new HashSet<Integer>());
         succ.put(stmt_count,new HashSet<Integer>());
         n.f0.accept(this, argu);
         String method_name = (String)argu;
         String label_name = (String)n.f1.accept(this, argu);
         succ.get(stmt_count).add(label_number.get(method_name).get(label_name));
         n.f1.accept(this, argu);
      }
      else if(p==2)
      {

      }
      return _ret;
   }

   /**
    * f0 -> "HSTORE"
    * f1 -> Temp()
    * f2 -> IntegerLiteral()
    * f3 -> Temp()
    */
   public R visit(HStoreStmt n, A argu) {
      R _ret=null;
      if(p==0)
      {
         n.f0.accept(this, argu);
         n.f1.accept(this, argu);
         n.f2.accept(this, argu);
         n.f3.accept(this, argu);
      }
      else if(p==1)
      {
         use.put(stmt_count,new HashSet<Integer>());
         def.put(stmt_count,new HashSet<Integer>());
         succ.put(stmt_count,new HashSet<Integer>());
         succ.get(stmt_count).add(stmt_count+1);
         use.get(stmt_count).add(Integer.parseInt((String)n.f1.accept(this, argu)));
         use.get(stmt_count).add(Integer.parseInt((String)n.f3.accept(this, argu)));
         n.f0.accept(this, argu);
         n.f1.accept(this, argu);
         n.f2.accept(this, argu);
         n.f3.accept(this, argu);
      }
      else if(p==2)
      {

      }
      return _ret;
   }

   /**
    * f0 -> "HLOAD"
    * f1 -> Temp()
    * f2 -> Temp()
    * f3 -> IntegerLiteral()
    */
   public R visit(HLoadStmt n, A argu) {
      R _ret=null;
      if(p==0)
      {
         n.f0.accept(this, argu);
         n.f1.accept(this, argu);
         n.f2.accept(this, argu);
         n.f3.accept(this, argu);
      }
      else if(p==1)
      {
         use.put(stmt_count,new HashSet<Integer>());
         def.put(stmt_count,new HashSet<Integer>());
         succ.put(stmt_count,new HashSet<Integer>());
         succ.get(stmt_count).add(stmt_count+1);
         use.get(stmt_count).add(Integer.parseInt((String)n.f2.accept(this, argu)));
         def.get(stmt_count).add(Integer.parseInt((String)n.f1.accept(this, argu)));
         n.f0.accept(this, argu);
         n.f1.accept(this, argu);
         n.f2.accept(this, argu);
         n.f3.accept(this, argu);
      }
      else if(p==2)
      {

      }
      return _ret;
   }

   /**
    * f0 -> "MOVE"
    * f1 -> Temp()
    * f2 -> Exp()
    */
   public R visit(MoveStmt n, A argu) {
      R _ret=null;
      if(p==0)
      {
         n.f0.accept(this, argu);
         n.f1.accept(this, argu);
         n.f2.accept(this, argu);
      }
      else if(p==1)
      {
         use.put(stmt_count,new HashSet<Integer>());
         def.put(stmt_count,new HashSet<Integer>());
         succ.put(stmt_count,new HashSet<Integer>());
         n.f0.accept(this, argu);
         String temp_index = (String)n.f1.accept(this, argu);
         succ.get(stmt_count).add(stmt_count+1);
         def.get(stmt_count).add(Integer.parseInt(temp_index));
         n.f2.accept(this, argu);
      }
      else if(p==2)
      {

      }
      return _ret;
   }

   /**
    * f0 -> "PRINT"
    * f1 -> SimpleExp()
    */
   public R visit(PrintStmt n, A argu) {
      R _ret=null;
      if(p==0)
      {
         n.f0.accept(this, argu);
         n.f1.accept(this, argu);
      }
      else if(p==1)
      {
         use.put(stmt_count,new HashSet<Integer>());
         def.put(stmt_count,new HashSet<Integer>());
         succ.put(stmt_count,new HashSet<Integer>());
         succ.get(stmt_count).add(stmt_count+1);
         n.f0.accept(this, argu);
         n.f1.accept(this, argu);
      }
      else if(p==2)
      {
         System.out.println("PRINT v0");
         n.f0.accept(this, argu);
         n.f1.accept(this, argu);
      }
      return _ret;
   }

   /**
    * f0 -> Call()
    *       | HAllocate()
    *       | BinOp()
    *       | SimpleExp()
    */
   public R visit(Exp n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      // if(p==1)
      // {
      //    System.out.println("stmt count is : "+stmt_count);
      //    System.out.println("use is : "+use.get(stmt_count));
      // }
      return _ret;
   }

   /**
    * f0 -> "BEGIN"
    * f1 -> StmtList()
    * f2 -> "RETURN"
    * f3 -> SimpleExp()
    * f4 -> "END"
    */
   public R visit(StmtExp n, A argu) {
      R _ret=null;
      if(p==0)
      {
         n.f0.accept(this, argu);
         n.f1.accept(this, argu);
         n.f2.accept(this, argu);
         stmt_count++;
         n.f3.accept(this, argu);
         n.f4.accept(this, argu);
      }
      else if(p==1)
      {
         n.f0.accept(this, argu);
         n.f1.accept(this, argu);
         stmt_count++;
         use.put(stmt_count,new HashSet<Integer>());
         def.put(stmt_count,new HashSet<Integer>());
         succ.put(stmt_count,new HashSet<Integer>());
         succ.get(stmt_count).add(-100); //doesn't have an successor
         n.f2.accept(this, argu);
         n.f3.accept(this, argu);
         // if(debug)
         // {
         //    System.out.println("simple exp done in stmt exp\n");
         // }
         n.f4.accept(this, argu);
         // if(debug)
         // {
         //    System.out.println("stmt exp is done\n");
         // }
      }
      return _ret;
   }

   /**
    * f0 -> "CALL"
    * f1 -> SimpleExp()
    * f2 -> "("
    * f3 -> ( Temp() )*
    * f4 -> ")"
    */
   public R visit(Call n, A argu) {
      R _ret=null;

      if(p==0)
      {
         n.f0.accept(this, argu);
         n.f1.accept(this, argu);
         n.f2.accept(this, argu);
         n.f3.accept(this, argu);
         n.f4.accept(this, argu);
      }
      else if(p==1)
      {
         String method_name = (String)argu;
         n.f0.accept(this, argu);
         n.f1.accept(this, argu);
         n.f2.accept(this, argu);
         succ.get(stmt_count).add(stmt_count+1);
         R list = n.f3.accept(this, argu);
         for(int i=0;i<((List<R>)list).size();i++)
         {
            String temp = (String)((List<R>)list).get(i);
            use.get(stmt_count).add(Integer.parseInt(temp));
         }
         n.f4.accept(this, argu);
         calls.put(method_name,calls.get(method_name)+1);
         int args = ((List<R>)list).size();
         if(args > proc_max__args.get(method_name))
         {
            proc_max__args.put(method_name,args);
         }
      }
      else if(p==2)
      {

      }
      return _ret;
   }

   /**
    * f0 -> "HALLOCATE"
    * f1 -> SimpleExp()
    */
   public R visit(HAllocate n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> Operator()
    * f1 -> Temp()
    * f2 -> SimpleExp()
    */
   public R visit(BinOp n, A argu) {
      R _ret=null;
      if(p==0)
      {
         n.f0.accept(this, argu);
         n.f1.accept(this, argu);
         n.f2.accept(this, argu);
      }
      else if(p==1)
      {
         n.f0.accept(this, argu);
         String temp_index = (String)n.f1.accept(this, argu); 
         succ.get(stmt_count).add(stmt_count+1);
         use.get(stmt_count).add(Integer.parseInt(temp_index));
         n.f2.accept(this, argu);
      }
      else if(p==2)
      {

      }
      return _ret;
   }

   /**
    * f0 -> "LE"
    *       | "NE"
    *       | "PLUS"
    *       | "MINUS"
    *       | "TIMES"
    *       | "DIV"
    */
   public R visit(Operator n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> Temp()
    *       | IntegerLiteral()
    *       | Label()
    */
   public R visit(SimpleExp n, A argu) {
      R _ret=null;
      if(p==0)
      {
         n.f0.accept(this, argu);
      }
      else if(p==1)
      {
         R temp = n.f0.accept(this, argu);
         if(n.f0.which==0)
         {
            //System.out.println("herep\n");
            use.get(stmt_count).add(Integer.parseInt((String)temp));
            // if(debug)
            // {
            //    System.out.println("temp is : "+(String)temp+ " and stmt count is : "+stmt_count);
            //    System.out.println("use is : "+use.get(stmt_count));
            // }
         }
         _ret = temp;
      }
      else if(p==2)
      {

      }
      
      return _ret;
   }

   /**
    * f0 -> "TEMP"
    * f1 -> IntegerLiteral()
    */
   public R visit(Temp n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      _ret = (R)n.f1.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public R visit(IntegerLiteral n, A argu) {
      R _ret=null;
      _ret = (R)n.f0.tokenImage;
      // if(debug)
      // {
      //    System.out.println("integer literal is : "+(String)_ret);
      // }
      return _ret;
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public R visit(Label n, A argu) {
      R _ret=null;
      if(p==0)
      {
         String label = n.f0.tokenImage;
         //System.out.println("label name is : "+label);
         _ret = (R)label;
      }
      else if(p==1)
      {
         String label = n.f0.tokenImage;
         //System.out.println("label name is : "+label);
         _ret = (R)label;
      }
      else if(p==2)
      {
      }
      return _ret;
   }

}
