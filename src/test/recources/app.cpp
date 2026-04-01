#include <iostream>
#include <vector>
#include <string>
#include <fstream>
#include <iomanip>
#include <algorithm>

using namespace std;

// Enum for Task Priority
enum Priority { LOW, MEDIUM, HIGH };

// Task Structure
struct Task {
    int id;
    string description;
    Priority priority;
    bool isCompleted;

    string getPriorityString() const {
        switch (priority) {
            case LOW: return "Low";
            case MEDIUM: return "Medium";
            case HIGH: return "High";
            default: return "Unknown";
        }
    }
};

class TaskManager {
private:
    vector<Task> tasks;
    int nextId;
    const string storageFile = "tasks.txt";

    void saveToFile() {
        ofstream outFile(storageFile);
        for (const auto& task : tasks) {
            outFile << task.id << "|" << task.description << "|" 
                    << task.priority << "|" << task.isCompleted << endl;
        }
        outFile.close();
    }

    void loadFromFile() {
        ifstream inFile(storageFile);
        if (!inFile) return;

        tasks.clear();
        string line;
        while (getline(inFile, line)) {
            Task task;
            size_t pos1 = line.find('|');
            size_t pos2 = line.find('|', pos1 + 1);
            size_t pos3 = line.find('|', pos2 + 1);

            task.id = stoi(line.substr(0, pos1));
            task.description = line.substr(pos1 + 1, pos2 - pos1 - 1);
            task.priority = static_cast<Priority>(stoi(line.substr(pos2 + 1, pos3 - pos2 - 1)));
            task.isCompleted = stoi(line.substr(pos3 + 1));
            
            tasks.push_back(task);
            if (task.id >= nextId) nextId = task.id + 1;
        }
        inFile.close();
    }

public:
    TaskManager() : nextId(1) {
        loadFromFile();
    }

    void addTask() {
        string desc;
        int prioInput;
        
        cout << "\nEnter Task Description: ";
        cin.ignore();
        getline(cin, desc);
        
        cout << "Priority (0: Low, 1: Medium, 2: High): ";
        cin >> prioInput;

        Task newTask = {nextId++, desc, static_cast<Priority>(prioInput), false};
        tasks.push_back(newTask);
        saveToFile();
        cout << "Task added successfully!\n";
    }

    void listTasks() {
        if (tasks.empty()) {
            cout << "\nNo tasks found.\n";
            return;
        }

        cout << "\n" << setfill('=') << setw(50) << "" << endl;
        cout << left << setw(5) << "ID" << setw(25) << "Description" 
             << setw(10) << "Priority" << "Status" << endl;
        cout << setfill('-') << setw(50) << "" << setfill(' ') << endl;

        for (const auto& task : tasks) {
            cout << left << setw(5) << task.id 
                 << setw(25) << (task.description.length() > 22 ? task.description.substr(0, 20) + "..." : task.description)
                 << setw(10) << task.getPriorityString()
                 << (task.isCompleted ? "[DONE]" : "[PENDING]") << endl;
        }
        cout << setfill('=') << setw(50) << "" << setfill(' ') << endl;
    }

    void markComplete() {
        int id;
        cout << "\nEnter Task ID to mark complete: ";
        cin >> id;

        auto it = find_if(tasks.begin(), tasks.end(), [id](const Task& t) { return t.id == id; });
        if (it != tasks.end()) {
            it->isCompleted = true;
            saveToFile();
            cout << "Task updated!\n";
        } else {
            cout << "Task ID not found.\n";
        }
    }

    void deleteTask() {
        int id;
        cout << "\nEnter Task ID to delete: ";
        cin >> id;

        auto it = remove_if(tasks.begin(), tasks.end(), [id](const Task& t) { return t.id == id; });
        if (it != tasks.end()) {
            tasks.erase(it, tasks.end());
            saveToFile();
            cout << "Task deleted.\n";
        } else {
            cout << "Task ID not found.\n";
        }
    }
};

void showMenu() {
    cout << "\n--- TASK MANAGER ---" << endl;
    cout << "1. Add Task" << endl;
    cout << "2. List Tasks" << endl;
    cout << "3. Mark Task Complete" << endl;
    cout << "4. Delete Task" << endl;
    cout << "5. Exit" << endl;
    cout << "Select an option: ";
}

int main() {
    TaskManager manager;
    int choice = 0;

    while (choice != 5) {
        showMenu();
        if (!(cin >> choice)) {
            cout << "Invalid input. Please enter a number.\n";
            cin.clear();
            cin.ignore(1000, '\n');
            continue;
        }

        switch (choice) {
            case 1: manager.addTask(); break;
            case 2: manager.listTasks(); break;
            case 3: manager.markComplete(); break;
            case 4: manager.deleteTask(); break;
            case 5: cout << "Exiting... Goodbye!\n"; break;
            default: cout << "Invalid choice. Try again.\n";
        }
    }

    return 0;
}