import React, { useEffect } from "react";

import ReactDOM from "react-dom/client";
import { v4 as uuidv4 } from "uuid";

import "./index.css";
import App from "./App";
import { UiProvider } from "./contexts/UiContext";
import { UuidProvider, useUuid } from "./contexts/UuidContext";
import reportWebVitals from "./reportWebVitals";

const root = ReactDOM.createRoot(
  document.getElementById("root") as HTMLElement
);

const AppWithUuid: React.FC = () => {
  const { uuid, setUuid } = useUuid();

  useEffect(() => {
    if (!uuid) {
      setUuid(uuidv4());
    }
  }, [uuid, setUuid]);

  return <App />;
};

root.render(
  <React.StrictMode>
    <UiProvider>
      <UuidProvider>
        <AppWithUuid />
      </UuidProvider>
    </UiProvider>
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
