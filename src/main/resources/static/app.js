const editButtons = document.querySelectorAll('.EditButton');
const tasksRefs = document.querySelectorAll('#tasksRef');
const tasksGroups = document.querySelectorAll('dd');

const saveButton = document.createElement("button");
saveButton.type = "submit";
saveButton.id = "saveEditButton";
saveButton.textContent = "Zapisz";

const cancelButton = document.createElement("button");
cancelButton.id = "cancelEditButton";
cancelButton.textContent = "Anuluj";

editButtons.forEach((button, index) => {
        button.addEventListener('click', (event) => {
            const button = event.target;
            const label = button.parentNode;
            const taskRef = tasksRefs.item(index);
            const tasksInGroup = tasksGroups.item(index);

            const spanDesc = label.querySelector('span');
            const deadlineDesc = label.querySelector('small');
            const inputDesc = document.createElement('input');
            const inputDeadline = document.createElement('input');
            inputDesc.type = 'text';
            inputDesc.value = spanDesc.textContent;
            inputDeadline.type = 'datetime-local';
            inputDeadline.value = deadlineDesc.textContent.replace('(', '').replace(')', '');

            console.log(tasksInGroup);
            changeTasksLabelsToInputs(tasksInGroup);

            label.replaceChild(inputDesc, spanDesc);
            label.replaceChild(inputDeadline, deadlineDesc);

            while (taskRef.firstChild) {
                taskRef.parentNode.insertBefore(taskRef.firstChild, taskRef);
            }
            taskRef.parentNode.removeChild(taskRef);
            button.parentNode.removeChild(button);

            tasksInGroup.parentNode.append(saveButton);
            tasksInGroup.parentNode.append(cancelButton);
        });
    }
);



function changeTasksLabelsToInputs(tasks) {
    'use strict';
    const tasksDesc = tasks.querySelectorAll('span');
    const tasksDescDel = tasks.querySelectorAll('del');
    tasksDesc.forEach(task => {
        const li = task.parentNode;
        const inputTaskDesc = document.createElement('input');
        inputTaskDesc.type = 'text';
        inputTaskDesc.value = task.textContent;

        li.replaceChildren(inputTaskDesc, li.firstChild);
    });
    tasksDescDel.forEach(task => {
        const li = task.parentNode;
        const inputTaskDescDel = document.createElement('input');
        inputTaskDescDel.type = 'text';
        inputTaskDescDel.value = task.textContent;
        li.replaceChildren(inputTaskDescDel, li.firstChild);
    });
}