import { useRef, useState } from "react";

import useMeasure from "react-use-measure";
import { FixedSizeList } from "react-window";

import {
  Divider,
  ListItem,
  ListItemButton,
  ListItemText,
  Paper,
  Typography,
} from "@mui/material";

interface LargeListProps {
  data: string[];
  header?: string;
  onSelect?: (index: number) => void;
  text?: boolean;
}

export default function LargeList({
  data,
  header,
  onSelect = () => {
    // Do nothing
  },
  text = false,
}: LargeListProps) {
  const [selectedIndex, setSelectedIndex] = useState(0);
  const [, { width }] = useMeasure();
  const listRef = useRef<HTMLDivElement>(null);

  const handleListItemClick = (
    event: React.MouseEvent<HTMLDivElement, MouseEvent>,
    index: number
  ) => {
    setSelectedIndex(index);
    onSelect(index);
  };

  return (
    <Paper
      sx={{
        width: "100%",
        height: "auto",
        // maxWidth: 360,
        minWidth: 0,
        // bgcolor: "background.paper",
        m: 0,
      }}
    >
      {header && <Typography p={1}>{header}</Typography>}
      <Divider />
      <FixedSizeList
        height={200}
        width={width - 20}
        itemSize={30}
        itemCount={data.length}
        // overscanCount={5}
        itemData={data}
        style={{ whiteSpace: "nowrap", margin: 10, border: "10px" }}
      >
        {({ index, style, data }) => {
          const selected: string = data[index];
          return (
            <ListItem
              ref={listRef}
              style={style}
              key={index}
              component="div"
              disablePadding
              dense
              onClick={(event: React.MouseEvent<HTMLDivElement, MouseEvent>) =>
                handleListItemClick(event, index)
              }
            >
              {text ? (
                <ListItemText primary={selected} />
              ) : (
                <ListItemButton
                  selected={index === selectedIndex}
                  dense
                  sx={{ height: style.height }}
                >
                  <ListItemText primary={selected} />
                </ListItemButton>
              )}
            </ListItem>
          );
        }}
      </FixedSizeList>
      <Divider />
      <Typography p={1}>Total: {data.length}</Typography>
    </Paper>
  );
}
