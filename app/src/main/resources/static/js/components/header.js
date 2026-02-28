Header.js
This file defines a reusable header component that appears at the top of every page. It dynamically changes based on the user's role (admin, doctor, and patient) and login state. It improves code reusability and reduces duplication across multiple HTML files.

You'll use JavaScript to insert navigation links, role selectors, and logout buttons depending on the context of the current page.

Task:
 Open header.js in IDE

you will build a renderHeader() function that:

Checks the current page.We don’t want to show the role-based header on the homepage

HINT :

    if (window.location.pathname.endsWith("/")) {
      localStorage.removeItem("userRole");
      localStorage.removeItem("token");
    }

Looks at the user’s role and login token in localStorage ,to determine which header layout to show.

HINT :

    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

Add the condition to check invalid handle button.

HINT :

    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
      localStorage.removeItem("userRole");
      alert("Session expired or invalid login. Please log in again.");
      window.location.href = "/";
      return;
    }

Injects the appropriate header HTML into the page.

HINT :

    if (role === "admin") {
      headerContent += `
        <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
        <a href="#" onclick="logout()">Logout</a>`;
    }

Repeat for :

doctor ➜ Show “Home” and Logout
patient ➜ Show “Login” and “Sign Up”
loggedPatient ➜ Show “Home”, “Appointments”, and Logout
Provides navigation buttons and logout functionality tailored to each user type.

HINT :

Start with an empty string: headerContent = ""
If role is admin  
  Add HTML string for "Add Doctor" button and Logout link
If role is doctor  
  Add "Home" button and Logout
If role is patient  
  Add Login and Signup buttons
If role is loggedPatient  
  Add Home, Appointments, and Logout

Finalize Header Injection. This replaces the contents of the #header div with the generated HTML.

HINT :

    headerDiv.innerHTML = headerContent;
    attachHeaderButtonListeners();

Attach Event Listeners because elements were dynamically created, you need to attach listeners after insertion.

Use document.getElementById(“someBtnId”)
Check if the element exists (in case the button is not for all roles)
Use .addEventListener(“click”, …) to attach a handler.

HINT :

After rendering the header  
Find buttons by ID  
Attach 'click' event listeners (e.g. to open a modal or clear storage)

Implementing Logout Functionality for clearing the session and going back to the start

Remove both token and userRole from localStorage.
Redirect to homepage using window.location.href = "/".
For patient we can retain their “role” as just patient, not loggedPatient, to show login/signup again.

HINT:

Create a function called logout  
Inside it, remove token and userRole  
Redirect to homepage
Create logoutPatient function  
Remove token  
Set role back to "patient"  
Redirect to patient dashboard
/*
  Step-by-Step Explanation of Header Section Rendering

  This code dynamically renders the header section of the page based on the user's role, session status, and available actions (such as login, logout, or role-switching).

  1. Define the `renderHeader` Function

     * The `renderHeader` function is responsible for rendering the entire header based on the user's session, role, and whether they are logged in.

  2. Select the Header Div

     * The `headerDiv` variable retrieves the HTML element with the ID `header`, where the header content will be inserted.
       ```javascript
       const headerDiv = document.getElementById("header");
       ```

  3. Check if the Current Page is the Root Page

     * The `window.location.pathname` is checked to see if the current page is the root (`/`). If true, the user's session data (role) is removed from `localStorage`, and the header is rendered without any user-specific elements (just the logo and site title).
       ```javascript
       if (window.location.pathname.endsWith("/")) {
         localStorage.removeItem("userRole");
         headerDiv.innerHTML = `
           <header class="header">
             <div class="logo-section">
               <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
               <span class="logo-title">Hospital CMS</span>
             </div>
           </header>`;
         return;
       }
       ```

  4. Retrieve the User's Role and Token from LocalStorage

     * The `role` (user role like admin, patient, doctor) and `token` (authentication token) are retrieved from `localStorage` to determine the user's current session.
       ```javascript
       const role = localStorage.getItem("userRole");
       const token = localStorage.getItem("token");
       ```

  5. Initialize Header Content

     * The `headerContent` variable is initialized with basic header HTML (logo section), to which additional elements will be added based on the user's role.
       ```javascript
       let headerContent = `<header class="header">
         <div class="logo-section">
           <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
           <span class="logo-title">Hospital CMS</span>
         </div>
         <nav>`;
       ```

  6. Handle Session Expiry or Invalid Login

     * If a user with a role like `loggedPatient`, `admin`, or `doctor` does not have a valid `token`, the session is considered expired or invalid. The user is logged out, and a message is shown.
       ```javascript
       if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
         localStorage.removeItem("userRole");
         alert("Session expired or invalid login. Please log in again.");
         window.location.href = "/";   or a specific login page
         return;
       }
       ```

  7. Add Role-Specific Header Content

     * Depending on the user's role, different actions or buttons are rendered in the header:
       - **Admin**: Can add a doctor and log out.
       - **Doctor**: Has a home button and log out.
       - **Patient**: Shows login and signup buttons.
       - **LoggedPatient**: Has home, appointments, and logout options.
       ```javascript
       else if (role === "admin") {
         headerContent += `
           <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
           <a href="#" onclick="logout()">Logout</a>`;
       } else if (role === "doctor") {
         headerContent += `
           <button class="adminBtn"  onclick="selectRole('doctor')">Home</button>
           <a href="#" onclick="logout()">Logout</a>`;
       } else if (role === "patient") {
         headerContent += `
           <button id="patientLogin" class="adminBtn">Login</button>
           <button id="patientSignup" class="adminBtn">Sign Up</button>`;
       } else if (role === "loggedPatient") {
         headerContent += `
           <button id="home" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
           <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
           <a href="#" onclick="logoutPatient()">Logout</a>`;
       }
       ```



  9. Close the Header Section



  10. Render the Header Content

     * Insert the dynamically generated `headerContent` into the `headerDiv` element.
       ```javascript
       headerDiv.innerHTML = headerContent;
       ```

  11. Attach Event Listeners to Header Buttons

     * Call `attachHeaderButtonListeners` to add event listeners to any dynamically created buttons in the header (e.g., login, logout, home).
       ```javascript
       attachHeaderButtonListeners();
       ```


  ### Helper Functions

  13. **attachHeaderButtonListeners**: Adds event listeners to login buttons for "Doctor" and "Admin" roles. If clicked, it opens the respective login modal.

  14. **logout**: Removes user session data and redirects the user to the root page.

  15. **logoutPatient**: Removes the patient's session token and redirects to the patient dashboard.

  16. **Render the Header**: Finally, the `renderHeader()` function is called to initialize the header rendering process when the page loads.
*/
   
