const API_URL =
    "http://localhost:8080/api/students";


// Get HTML elements

const studentForm =
    document.getElementById("studentForm");

const clearBtn =
    document.getElementById("clearBtn");

const performanceTable =
    document.getElementById("performanceTable");





document.addEventListener(
    "DOMContentLoaded",
    loadStudents
);





studentForm.addEventListener(
    "submit",
    addStudent
);





clearBtn.addEventListener(
    "click",
    clearFields
);





async function addStudent(event) {


    event.preventDefault();


    // Get values from form

    const name =
        document
            .getElementById("studentName")
            .value
            .trim();


    const math =
        parseFloat(
            document
                .getElementById("math")
                .value
        );


    const science =
        parseFloat(
            document
                .getElementById("science")
                .value
        );


    const english =
        parseFloat(
            document
                .getElementById("english")
                .value
        );



    // Validate name

    if (name === "") {

        alert(
            "Please enter a valid student name."
        );

        return;

    }



    // Validate numbers

    if (
        isNaN(math) ||
        isNaN(science) ||
        isNaN(english)
    ) {

        alert(
            "Please enter numerical values for marks."
        );

        return;

    }



    // Validate range

    if (
        math < 0 || math > 100 ||
        science < 0 || science > 100 ||
        english < 0 || english > 100
    ) {

        alert(
            "Marks must be between 0 and 100."
        );

        return;

    }



    // Create object

    const student = {

        name: name,

        math: math,

        science: science,

        english: english

    };



    try {


        // Send data to Java API

        const response =
            await fetch(
                API_URL,
                {

                    method: "POST",

                    headers: {

                        "Content-Type":
                            "application/json"

                    },

                    body:
                        JSON.stringify(student)

                }
            );



        // Check response

        if (!response.ok) {

            throw new Error(
                "Unable to add student."
            );

        }



        // Convert response to JSON

        const data =
            await response.json();



        // Display latest calculation

        displaySummary(data);



        // Reload table

        loadStudents();



        // Clear form

        clearFields();


    }
    catch (error) {


        console.error(error);


        alert(
            "Could not connect to the Java backend."
        );

    }

}



// ==========================================
// LOAD ALL STUDENTS
// ==========================================

async function loadStudents() {


    try {


        const response =
            await fetch(API_URL);


        if (!response.ok) {

            throw new Error(
                "Unable to load students."
            );

        }


        const students =
            await response.json();


        // Clear existing table

        performanceTable.innerHTML = "";


        // Add every student

        students.forEach(
            student => {

                addStudentToTable(student);

            }
        );


    }
    catch (error) {


        console.error(error);


        alert(
            "Could not load student records."
        );

    }

}





function addStudentToTable(student) {


    const row =
        document.createElement("tr");



    row.innerHTML = `

        <td>
            ${student.name}
        </td>

        <td>
            ${student.math.toFixed(1)}
        </td>

        <td>
            ${student.science.toFixed(1)}
        </td>

        <td>
            ${student.english.toFixed(1)}
        </td>

        <td>
            ${student.total.toFixed(2)}
        </td>

        <td>
            ${student.average.toFixed(2)}%
        </td>

        <td>
            ${student.grade}
        </td>

        <td>

            <button
                class="delete-btn"
                onclick="deleteStudent(${student.id})">

                Delete

            </button>

        </td>

    `;


    performanceTable.appendChild(row);

}





function displaySummary(student) {


    document.getElementById("total")
        .textContent =
        student.total.toFixed(2);


    document.getElementById("average")
        .textContent =
        student.average.toFixed(2)
        + "%";


    document.getElementById("grade")
        .textContent =
        student.grade;

}





async function deleteStudent(id) {


    const confirmation =
        confirm(
            "Are you sure you want to delete this student?"
        );


    if (!confirmation) {

        return;

    }



    try {


        const response =
            await fetch(
                `${API_URL}/${id}`,
                {

                    method: "DELETE"

                }
            );


        if (!response.ok) {

            throw new Error(
                "Unable to delete student."
            );

        }


        // Reload table

        loadStudents();


    }
    catch (error) {


        console.error(error);


        alert(
            "Could not delete the student."
        );

    }

}





function clearFields() {


    document.getElementById(
        "studentName"
    ).value = "";


    document.getElementById(
        "math"
    ).value = "";


    document.getElementById(
        "science"
    ).value = "";


    document.getElementById(
        "english"
    ).value = "";


    document.getElementById(
        "studentName"
    ).focus();

}