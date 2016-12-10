
#define NULL ( (void *) 0)
extern void *heap_start;
extern void *heap_limit;

static struct memory_region *firstfree;

struct memory_region;
void *malloc(unsigned int size);

void joinRegions(struct memory_region *prev, struct memory_region *curr, struct memory_region *next);
	
void free (void *target);
