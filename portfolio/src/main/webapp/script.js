// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Our fates are sealed. But I think we have one move left: We can try. – Eleanor Shellstrop', 
      'We have no plan. No one’s coming to save us. So… I’m going to do it. – Michael', 
      'I argue that we choose to be good because of our bonds with other people and our innate desire to treat them with dignity. Simply put, we are not in this alone. – Chidi Anagonye', 
      'In football, trying to run out the clock and hoping for the best never works. It’s called prevent defense. You don’t take any chances and just try and hold on to your lead. But prevent defense just PREVENTS you from winning! It’s always better to try something. – Jason Mendoza'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}
function addCoursework(){
    const courses = ['Engineering Design & Communication', 
        'Computational Methods in Engineering','Matrices & Vector Spaces',
        'Computer Architecture','Data Structures & Algorithms',
        'Product Managment','Web Design & Narrative','Foundations of Education'];
    const courselist = document.createElement("UL");
    for (var i = 0; i < courses.length; i++){
        var course = document.createElement("LI");
        course.innerText = courses[i];
        courselist.appendChild(course);
    }
    const courseContainer = document.getElementById('course-container');
    courseContainer.appendChild(courselist);
}
async function helloJamila(){
    const response = await fetch('/data');
    const greeting = await response.text();
    document.getElementById('greeting').innerText = greeting;
}
async function fetchComments(){
    const container = document.getElementById('comments');
    console.log("Fetching Comments");
    const comments = fetch('/data')
    .then(response => response.json())
    .then(message => {
        container.innerText = "";
        console.log(message);
        message.forEach((msg) => {
            console.log(msg);
            container.innerText += msg.message + "\n";
        });
    }
    );
}
async function fetchTranslation(){
    const container = document.getElementById('result');
    console.log("requesting translation");
    fetch('/translate')
    .then(response => response.json())
    .then(translation => {
        console.log(translation);
        if(translation.length == 0){
            return;
        }
        container.innerText = translation[0];
        }
    );
}