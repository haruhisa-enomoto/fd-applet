/* eslint-disable @typescript-eslint/no-empty-function */
import { createContext, useContext, useState } from "react";

type SelectionContextType = {
  selected: string[];
  setSelected: (selected: string[]) => void;
  secondarySelected: string[];
  setSecondarySelected: (secondarySelected: string[]) => void;
  highlighted: string[];
  setHighlighted: (highlighted: string[]) => void;
};

const SelectionContext = createContext<SelectionContextType>({
  selected: [],
  setSelected: () => {},
  secondarySelected: [],
  setSecondarySelected: () => {},
  highlighted: [],
  setHighlighted: () => {}
});

interface SelectionProviderProps {
  children: React.ReactNode;
}

export const SelectionProvider: React.FC<SelectionProviderProps> = ({
  children,
}) => {
  const [selected, setSelected] = useState<string[]>([]);
  const [secondarySelected, setSecondarySelected] = useState<string[]>([]);
  const [highlighted, setHighlighted] = useState<string[]>([]);

  return (
    <SelectionContext.Provider
      value={{
        selected,
        setSelected,
        secondarySelected,
        setSecondarySelected,
        highlighted,
        setHighlighted,
      }}
    >
      {children}
    </SelectionContext.Provider>
  );
};

export const useSelection = () => useContext(SelectionContext);