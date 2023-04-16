/* eslint-disable @typescript-eslint/no-empty-function */
import { createContext, useContext, useState } from "react";

import { AlertColor } from "@mui/material/Alert";

export type NotifyContents = {
  duration?: number;
  severity?: AlertColor;
  message: string;
};

interface UiContextType {
  openNotify: boolean;
  setOpenNotify: (value: boolean) => void;
  notifyStatus: NotifyContents;
  setNotifyStatus: (value: NotifyContents) => void;
  openBack: boolean;
  setOpenBack: (value: boolean) => void;
}

const UiContext = createContext<UiContextType>({
  openNotify: false,
  setOpenNotify: () => {},
  notifyStatus: {
    message: "",
  },
  setNotifyStatus: () => {},
  openBack: false,
  setOpenBack: () => {},
});

interface UiProviderProps {
  children: React.ReactNode;
}

export const UiProvider: React.FC<UiProviderProps> = ({ children }) => {
  const [openNotify, setOpenNotify] = useState(false);
  const [notifyStatus, setNotifyStatus] = useState<NotifyContents>({
    duration: 0,
    severity: "success",
    message: "",
  });
  const [openBack, setOpenBack] = useState(false);

  return (
    <UiContext.Provider
      value={{
        openNotify,
        setOpenNotify,
        notifyStatus,
        setNotifyStatus,
        openBack,
        setOpenBack,
      }}
    >
      {children}
    </UiContext.Provider>
  );
};

export const useUi = () => useContext(UiContext);
