import { Avatar, AvatarGroup } from "@chakra-ui/react";

interface CustomAvatarGroupProps extends React.ComponentProps<
  typeof AvatarGroup
> {
  items: { name: string; src: string }[];
max?: number;
}

export function CustomAvatarGroup({ items, max = 3, ...props }: CustomAvatarGroupProps) {
    const displayedItems = items.slice(0, max);
    const extraCount = items.length - max;
  return (
    <AvatarGroup size="lg" stacking="last-on-top" {...props}>
      {displayedItems.map((item) => (
        <Avatar.Root key={item.name}>
          <Avatar.Fallback name={item.name} />
          <Avatar.Image src={item.src} />
        </Avatar.Root>
      ))}
      {extraCount > 0 &&
      <Avatar.Root>
         <Avatar.Fallback>+{extraCount}</Avatar.Fallback>
      </Avatar.Root>}
    </AvatarGroup>
  );
}
