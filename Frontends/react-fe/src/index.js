import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import './main.css';
import reportWebVitals from './reportWebVitals';
import Login from "./pages/Login";
import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import Main from "./pages/Main";
import Details from "./pages/Details";
import NewContract from "./pages/NewContract";

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <Router>
        <Routes>
            <Route exact path={"/home"} element={<Main />} />
            <Route exact path={"/"} element={<Login />} />
            <Route exact path={"/contract/:id"} element={<Details />} />
            <Route exact path={"/new"} element={<NewContract />} />
        </Routes>
    </Router>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
