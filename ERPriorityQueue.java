import java.util.ArrayList;
import java.util.HashMap;

public class ERPriorityQueue{
	public ArrayList<Patient>  patients; //list of patients
	public HashMap<String,Integer>  nameToIndex; //patient name/priority position key value pairs

	public ERPriorityQueue(){
		patients = new ArrayList<Patient>();
		patients.add( new Patient("dummy", 0.0) ); //using a dummy node so that indexing starts at 1, not 0
		nameToIndex  = new HashMap<String,Integer>();
	}

	//heap behavior as priority queue
	private int parent(int i){
		return i/2;
	}

	private int leftChild(int i){
	    return 2*i;
	}

	private int rightChild(int i){
	    return 2*i+1;
	}

	//determining if patient is a leaf of the heap
	public boolean isLeaf(int i){
		if (leftChild(i) <= patients.size() - 1){
			return false;
		}
		else{
			return true;
		}
	}

	//verifying heap validity
	public boolean legalHeap() {
		for (int i = 1; i <= (patients.size() - 1)/2; i++) {
			if (patients.get(i).priority > patients.get(i * 2).priority) return false;
			if (patients.size() - 1 >= i * 2 + 1 && patients.get(i).priority > patients.get(i * 2 + 1).priority) return false;
		}
		return true;
	}

	//swapping positions of two patients
	public void swap(int i, int j){
		Patient temp1 = patients.get(i);
		Patient temp2 = patients.get(j);
		patients.set(i , temp2);
		patients.set(j , temp1);
		nameToIndex.put(temp1.getName() , j);
		nameToIndex.put(temp2.getName() , i);
	}

	public void upHeap(int i){
		while ( i > 1 && patients.get(i).getPriority() < patients.get(parent(i)).getPriority()){
			swap(i,parent(i));
			i = parent(i);
		}
	}

	//helper method
	public Integer upHeapWithIndex(int i){
		while ( i > 1 && patients.get(i).getPriority() < patients.get(parent(i)).getPriority()){
			swap(i,parent(i));
			i = parent(i);
		}
		return i;
	}

	public void downHeap(int i){
        while (leftChild(i) < patients.size()){
			int child = leftChild(i);
			if (child < patients.size() - 1){
				if (patients.get(child + 1).getPriority() < patients.get(child).getPriority()){
					child = child + 1;
				}
			}
			if (patients.get(child).getPriority() < patients.get(i).getPriority()){
				swap(i , child);
				i = child;
			}
			else{
				return;
			}
		}
	}

	//checking if the queue contains the patient
	public boolean contains(String name){
        Integer priorityIndex = nameToIndex.get(name);
		if (priorityIndex == null){
			return false;
		}
        return true;
	}

	//getting the priority of the patient
	public double getPriority(String name){
		Integer priorityIndex = nameToIndex.get(name);
		if (priorityIndex == null){
			return -1;
		}
        return patients.get(priorityIndex).getPriority();
	}

	public double getMinPriority(){
		if (patients.size() == 1){
			return -1;
		}
		return patients.get(1).getPriority();

	}

	//admitting the first patient in line
	public String removeMin(){
		if (patients.size() == 1){
			return null;
		}
		if (patients.size() == 2){
			String minPriorityPatient = patients.get(1).getName();
			nameToIndex.remove(patients.get(1).getName());
			patients.remove(1);
			return minPriorityPatient;
		}
		String minPriorityPatient = patients.get(1).getName();
		nameToIndex.remove(patients.get(1).getName());
		patients.set(1 , patients.get(patients.size() - 1));
		nameToIndex.put(patients.get(1).getName(), 1);
		patients.remove(patients.size() - 1);

		downHeap(1);

        return minPriorityPatient;
	}

	public String peekMin(){
        if (patients.size() == 1){
			return null;
		}
		return patients.get(1).getName();
	}

	//adds patient to the queue with a given priority
	public boolean  add(String name, double priority){
		Integer priorityIndex = nameToIndex.get(name);
		if (priorityIndex != null){
			return false;
		}

        Patient newPatient = new Patient(name , priority);
        patients.add(newPatient);
		Integer index = upHeapWithIndex(patients.size() - 1);
		nameToIndex.put(name , index);
		return true;
	}

	//adds patient to the queue
	public boolean  add(String name){
		Integer priorityIndex = nameToIndex.get(name);
		if (priorityIndex != null){
			return false;
		}

		Patient newPatient = new Patient(name , Double.POSITIVE_INFINITY);
		patients.add(newPatient);

		nameToIndex.put(name , patients.size() - 1);
		return true;
	}

	//removes patient from queue
	public boolean remove(String name){
		if (nameToIndex.get(name) == null){
			return false;
		}

        Integer index = nameToIndex.get(name);
		patients.set(index , patients.get(patients.size() - 1));
		nameToIndex.put(patients.get(patients.size() - 1).getName() , index);
		patients.remove(patients.size() - 1);
		nameToIndex.remove(name);
		if (index < patients.size()){
			Integer curIndex = upHeapWithIndex(index);
			downHeap(curIndex);
		}

        return true;
	}

	//changes priority of the given patient to the new priority
	public boolean changePriority(String name, double priority){
		if (nameToIndex.get(name) == null){
			return false;
		}
		Integer index = nameToIndex.get(name);
		patients.get(index).setPriority(priority);
		Integer curIndex = upHeapWithIndex(index);
		downHeap(curIndex);
		return true;
	}

	//remove all patients above the threshold
	public ArrayList<Patient> removeUrgentPatients(double threshold){
		ArrayList<Patient> urgentPatients = new ArrayList<>();

		for (int i = 1 ; i < patients.size() ; i++){
			if (patients.get(i).getPriority() <= threshold){
				urgentPatients.add(patients.get(i));
			}
		}
		for (Patient curPatient : urgentPatients){
			this.remove(curPatient.getName());
		}
        return urgentPatients;
	}

	//remove all patients below the threshold
	public ArrayList<Patient> removeNonUrgentPatients(double threshold){
		ArrayList<Patient> urgentPatients = new ArrayList<>();

		for (int i = 1 ; i < patients.size() ; i++){
			if (patients.get(i).getPriority() >= threshold){
				urgentPatients.add(patients.get(i));
			}
		}

		for (Patient curPatient : urgentPatients){
			this.remove(curPatient.getName());

		}
		return urgentPatients;
	}



	static class Patient{
		private String name;
		private double priority;

		Patient(String name,  double priority){
			this.name = name;
			this.priority = priority;
		}

		Patient(Patient otherPatient){
			this.name = otherPatient.name;
			this.priority = otherPatient.priority;
		}

		double getPriority() {
			return this.priority;
		}

		void setPriority(double priority) {
			this.priority = priority;
		}

		String getName() {
			return this.name;
		}

		@Override
		public String toString(){
			return this.name + " - " + this.priority;
		}

		public boolean equals(Object obj){
			if (!(obj instanceof  ERPriorityQueue.Patient)) return false;
			Patient otherPatient = (Patient) obj;
			return this.name.equals(otherPatient.name) && this.priority == otherPatient.priority;
		}
	}
}



