import {
  MenuItem,
  Divider,
  FormControl,
  InputLabel,
  Select,
  SelectChangeEvent,
  Typography,
  FormHelperText,
  Box,
} from "@mui/material";

import LargerTooltip from "./LargerTooltip";

export type ComboOption = {
  key: string;
  label: string;
  description?: string;
};

function getDescriptionForKey(
  options: ComboOption[],
  key: string
): string | undefined {
  const option = options.find((option) => option.key === key);
  return option ? option.description : undefined;
}

interface ComboProps {
  title: string;
  options: ComboOption[];
  selected: string;
  setSelected: (value: string) => void;
  showTooltips?: boolean;
  showDescriptions?: boolean;
  helperText?: string;
}

export function Combo({
  title,
  options,
  selected,
  setSelected,
  showTooltips = true,
  showDescriptions = false,
  helperText,
}: ComboProps) {
  const handleChange = (event: SelectChangeEvent) => {
    setSelected(event.target.value);
  };

  return (
    <>
      <FormControl
        fullWidth
        // size="small"
        sx={{
          // m: 1,
          // width: 100,
          // display: "flex",
          //  maxWidth: 100
        }}
      >
        <InputLabel>{title}</InputLabel>
        <Select value={selected} label={title} onChange={handleChange}>
          {options.map((option, index) =>
            option.key.startsWith("-") ? <Divider key={index} /> :
              <MenuItem key={option.key} value={option.key}>
                {showTooltips ?
                  <LargerTooltip
                    arrow
                    enterDelay={0}
                    enterNextDelay={0}
                    leaveDelay={0}
                    key={option.key}
                    title={option.description}
                    placement="right"
                  >
                    <Box sx={{ width: "100%" }}>
                      {option.label}
                    </Box>
                  </LargerTooltip>
                  :
                  option.label}
              </MenuItem>
          )}
        </Select>
        {helperText && (
          <FormHelperText>{helperText}</FormHelperText>
        )}
        {showDescriptions && (
          <Typography m={1} whiteSpace="pre-line">
            {getDescriptionForKey(options, selected)}
          </Typography>
        )}
      </FormControl>
    </>
  );
}
