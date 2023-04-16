/* eslint-disable @typescript-eslint/no-empty-function */
import { createContext, useContext, useState } from "react";

interface UuidContextType {
  uuid: string | null;
  setUuid: (value: string | null) => void;
}

const defaultValue: UuidContextType = {
  uuid: null,
  setUuid: () => {},
};

const UuidContext = createContext<UuidContextType>(defaultValue);

interface UuidProviderProps {
  children: React.ReactNode;
}

export const UuidProvider: React.FC<UuidProviderProps> = ({ children }) => {
  const [uuid, setUuid] = useState<string | null>(null);
  return (
    <UuidContext.Provider value={{ uuid, setUuid }}>
      {children}
    </UuidContext.Provider>
  );
};

export const useUuid = () => useContext(UuidContext);
